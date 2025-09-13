/*
 * Copyright 2021 Ververica GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ververica.lablatency.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.lablatency.event.EnrichedProduct;
import com.ververica.lablatency.event.Product;
import com.ververica.lablatency.event.ProductRecord;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.flink.api.java.typeutils.TypeExtractor.getForClass;

/** EnrichingJob: enrich products with location asynchronously. */
public class EnrichingJobAsync {
    private static final Logger LOG = LoggerFactory.getLogger(EnrichingJobAsync.class);

    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromArgs(args);
        LOG.info("params: " + params.getProperties());

        Configuration config = GlobalConfiguration.loadConfiguration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        final String jobName = params.get("job-name", EnrichingJobAsync.class.getSimpleName());
        final String kafkaAddress = params.get("kafka", "localhost:9093");
        final String topic = params.get("topic", "lablatency");
        final String group = params.get("group", "lablatency");

        final int outOfOrderness = params.getInt("out-of-orderness", 250);
        final int responseTimeMin = params.getInt("response-time-min", 1);
        final int responseTimeMax = params.getInt("response-time-max", 6);
        final int cacheExpiryMs = params.getInt("cache-expiry-ms", 1000);

        // Using new Kafka connector API for Flink 1.20
        KafkaSource<ProductRecord> consumer = KafkaSource.<ProductRecord>builder()
                .setBootstrapServers(kafkaAddress)
                .setTopics(topic)
                .setGroupId(group)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new KafkaDeSerSchema())
                .build();

        DataStream<Tuple2<Product, Long>> sourceStream =
                env.fromSource(consumer, WatermarkStrategy.noWatermarks(), "KafkaSource")
                        .name("KafkaSource")
                        .uid("KafkaSource")
                        .assignTimestampsAndWatermarks(
                                WatermarkStrategy.<ProductRecord>forBoundedOutOfOrderness(
                                                Duration.ofMillis(outOfOrderness))
                                        .withTimestampAssigner(
                                                (element, timestamp) -> element.getTimestamp())
                                        .withIdleness(Duration.ofSeconds(1)))
                        .name("Watermarks")
                        .uid("Watermarks")
                        .flatMap(new ProductDeserializer())
                        .name("Deserialization")
                        .uid("Deserialization");

        DataStream<EnrichedProduct> enrichedStream =
                AsyncDataStream.unorderedWait(
                                sourceStream.keyBy(x -> x.f0.getProduct_name()),
                                new EnrichProductWithLocationInfoAsync(
                                        cacheExpiryMs, responseTimeMin, responseTimeMax),
                                0,
                                TimeUnit.MILLISECONDS,
                                30)
                        .name("MainOperator:Enrich");
        enrichedStream
                .print("Enriched Products")
                .name("NormalOutput")
                .uid("NormalOutput")
                .disableChaining();

        env.execute(jobName);
    }

    /** Get ProductRecord from Kafka ConsumerRecord. */
    static class KafkaDeSerSchema implements KafkaRecordDeserializationSchema<ProductRecord> {

        private static final long serialVersionUID = 1L;

        @Override
        public void deserialize(
                ConsumerRecord<byte[], byte[]> consumerRecord, Collector<ProductRecord> collector) {
            collector.collect(
                    new ProductRecord(
                            consumerRecord.timestamp(),
                            consumerRecord.key(),
                            consumerRecord.value(),
                            consumerRecord.partition()));
        }

        @Override
        public TypeInformation<ProductRecord> getProducedType() {
            return getForClass(ProductRecord.class);
        }
    }

    /** Deserializes ProductRecord into Product. */
    public static class ProductDeserializer
            extends RichFlatMapFunction<ProductRecord, Tuple2<Product, Long>> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOG = LoggerFactory.getLogger(ProductDeserializer.class);

        private ObjectMapper objectMapper;

        @Override
        public void open(final Configuration parameters) throws Exception {
            super.open(parameters);
            this.objectMapper = new ObjectMapper();
        }

        @Override
        public void flatMap(
                final ProductRecord kafkaRecord,
                final Collector<Tuple2<Product, Long>> out) {
            final Product product;
            try {
                product =
                        this.objectMapper.readValue(kafkaRecord.getValue(), Product.class);
            } catch (IOException e) {
                LOG.error("Failed to deserialize: " + e.getLocalizedMessage());
                return;
            }
            out.collect(new Tuple2<>(product, kafkaRecord.getTimestamp()));
        }
    }

    /** Enrich product with location asynchronously. */
    public static class EnrichProductWithLocationInfoAsync
            extends RichAsyncFunction<Tuple2<Product, Long>, EnrichedProduct> {
        private static final long serialVersionUID = 2L;

        private transient LocationInfoServiceClient locationInfoServiceClient;
        private transient Map<String, Tuple2<Long, String>> cache;

        private static final int PROCESSING_TIME_DELAY_WINDOW_SIZE = 10_000;
        private transient DescriptiveStatisticsHistogram processingTimeDelay;

        private final int cacheExpiryMs;
        private Counter cacheSizeMetric;
        private Counter servedFromCacheMetric;
        private final int responseTimeMin;
        private final int responseTimeMax;

        /**
         * Creates a new enrichment function with a (local) cache that expires after the given
         * number of milliseconds.
         */
        public EnrichProductWithLocationInfoAsync(
                int cacheExpiryMs, int responseTimeMin, int responseTimeMax) {
            this.cacheExpiryMs = cacheExpiryMs;
            this.responseTimeMin = responseTimeMin;
            this.responseTimeMax = responseTimeMax;
        }

        @Override
        public void open(final Configuration parameters) {
            locationInfoServiceClient =
                    new LocationInfoServiceClient(this.responseTimeMin, this.responseTimeMax);
            processingTimeDelay =
                    getRuntimeContext()
                            .getMetricGroup()
                            .histogram(
                                    "processingTimeDelay",
                                    new DescriptiveStatisticsHistogram(
                                            PROCESSING_TIME_DELAY_WINDOW_SIZE));
            cache = new HashMap<>();
            servedFromCacheMetric = getRuntimeContext().getMetricGroup().counter("servedFromCache");
            cacheSizeMetric = getRuntimeContext().getMetricGroup().counter("cacheSize");
        }

        @Override
        public void asyncInvoke(
                Tuple2<Product, Long> product,
                ResultFuture<EnrichedProduct> resultFuture) {
            String product_name = product.f0.getProduct_name();
            final String locationInfo;

            Tuple2<Long, String> cachedLocationInfo = cache.get(product_name);
            if (cachedLocationInfo != null
                    && System.currentTimeMillis() - cachedLocationInfo.f0 <= cacheExpiryMs) {
                locationInfo = cachedLocationInfo.f1;
                EnrichedProduct enrichedProduct =
                        new EnrichedProduct(product.f0, locationInfo);
                resultFuture.complete(Collections.singleton(enrichedProduct));
                servedFromCacheMetric.inc();
            } else {
                locationInfoServiceClient.asyncGetLocationInfo(
                        product.f0.getProduct_name(),
                        new LocationServiceCallBack(resultFuture, product, product_name));
            }
        }

        private class LocationServiceCallBack implements Consumer<String> {
            private final ResultFuture<EnrichedProduct> resultFuture;
            private final Tuple2<Product, Long> product;
            private final String product_name;

            public LocationServiceCallBack(
                    final ResultFuture<EnrichedProduct> resultFuture,
                    final Tuple2<Product, Long> product,
                    final String product_name) {
                this.resultFuture = resultFuture;
                this.product = product;
                this.product_name = product_name;
            }

            @Override
            public void accept(final String locationInfo) {
                EnrichedProduct enrichedProduct =
                        new EnrichedProduct(product.f0, locationInfo);
                resultFuture.complete(Collections.singleton(enrichedProduct));

                processingTimeDelay.update(System.currentTimeMillis() - product.f1);

                if (cache.put(product_name, new Tuple2<>(System.currentTimeMillis(), locationInfo))
                        == null) {
                    cacheSizeMetric.inc();
                }
            }
        }
    }

    /** Location service client. */
    public static class LocationInfoServiceClient {
        private static final int LEN_OF_INFO = 100;
        private static final ExecutorService pool =
                Executors.newFixedThreadPool(
                        30,
                        new ThreadFactory() {
                            private final ThreadFactory threadFactory =
                                    Executors.defaultThreadFactory();

                            @Override
                            public Thread newThread(Runnable r) {
                                Thread thread = threadFactory.newThread(r);
                                thread.setName("location-service-client-" + thread.getName());
                                return thread;
                            }
                        });
        private final int responseTimeMin;
        private final int responseTimeMax;

        /**
         * Creates a new enrichment function with a (local) cache that expires after the given
         * number of milliseconds.
         */
        public LocationInfoServiceClient(int responseTimeMin, int responseTimeMax) {
            this.responseTimeMin = responseTimeMin;
            this.responseTimeMax = responseTimeMax;
        }
        /** Gets the info for the given location. */
        public String getLocationInfo(String product_name) {
            return new LocationInfoSupplier().get();
        }

        /** Asynchronous getter for the info for the given location. */
        public void asyncGetLocationInfo(String product_name, Consumer<String> callback) {
            CompletableFuture.supplyAsync(new LocationInfoSupplier(), pool)
                    .thenAcceptAsync(
                            callback,
                            ForkJoinPool.commonPool());
        }

        private class LocationInfoSupplier implements Supplier<String> {
            @Override
            public String get() {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(responseTimeMax - responseTimeMin) + responseTimeMin);
                } catch (InterruptedException e) {
                    // Swallowing interruption here
                }
                return RandomStringUtils.randomAlphabetic(LEN_OF_INFO);
            }
        }
    }
}
