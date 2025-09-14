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
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
//import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.lablatency.event.Product;
import com.ververica.lablatency.event.ProductRecord;
import com.ververica.lablatency.event.WindowedProduct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

import static org.apache.flink.api.java.typeutils.TypeExtractor.getForClass;

/** WindowingJob */
public class WindowingJob {
    private static final Logger LOG = LoggerFactory.getLogger(WindowingJob.class);

    public static void main(String[] args) throws Exception {


        ParameterTool params = ParameterTool.fromArgs(args);
        LOG.info("Starting WindowingJob with params: {}", params.getProperties());

        Configuration config = GlobalConfiguration.loadConfiguration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        final String jobName = params.get("job-name", WindowingJob.class.getSimpleName());
        final String kafkaAddress = params.get("kafka", "localhost:9093");
        final String topic = params.get("topic", "lablatency");
        final String group = params.get("group", "lablatency");

        final int slideSize = params.getInt("slide-size", 10);
        final int outOfOrderness = params.getInt("out-of-orderness", 250);

        // Using new Kafka connector API for Flink 1.20
        KafkaSource<ProductRecord> consumer = KafkaSource.<ProductRecord>builder()
                .setBootstrapServers(kafkaAddress)
                .setTopics(topic)
                .setGroupId(group)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(new KafkaDeSerSchema())
                .build();

        DataStream<Product> sourceStream =
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

        SingleOutputStreamOperator<WindowedProduct> aggregatedPerProductName =
                sourceStream
                        .keyBy(Product::getProduct_name)
                        .window(
                                SlidingEventTimeWindows.of(
                                        Duration.ofSeconds(60), Duration.ofSeconds(slideSize)))
                        .aggregate(
                                new ProductAggregateFunction(),
                                new ProductProcessWindowFunction())
                        .name("WindowedAggregation")
                        .uid("WindowedAggregation")
                        .disableChaining();

        aggregatedPerProductName
                .print("Windowed Products")
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
            extends RichFlatMapFunction<ProductRecord, Product> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOG = LoggerFactory.getLogger(ProductDeserializer.class);

        private ObjectMapper objectMapper;

        @Override
        public void open(final Configuration parameters) throws Exception {
            super.open(parameters);
            objectMapper = new ObjectMapper();
        }

        @Override
        public void flatMap(final ProductRecord kafkaRecord, final Collector<Product> out) {
            try {
                final Product product = objectMapper.readValue(kafkaRecord.getValue(), Product.class);
                out.collect(product);
            } catch (IOException e) {
                LOG.error("Failed to deserialize product", e);
            }
        }
    }

    /** Incrementally aggregate products. */
    public static class ProductAggregateFunction
            implements AggregateFunction<Product, Tuple2<Long, Double>, Tuple2<Long, Double>> {

        @Override
        public Tuple2<Long, Double> createAccumulator() {
            return new Tuple2<>(0L, 0.0);
        }

        @Override
        public Tuple2<Long, Double> add(Product product, Tuple2<Long, Double> accumulator) {
            return new Tuple2<>(accumulator.f0 + 1, accumulator.f1 + product.getPrice());
        }

        @Override
        public Tuple2<Long, Double> getResult(Tuple2<Long, Double> accumulator) {
            return new Tuple2<>(accumulator.f0, accumulator.f1);
        }

        @Override
        public Tuple2<Long, Double> merge(Tuple2<Long, Double> a, Tuple2<Long, Double> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }

    /** ProcessWindowFunction produces WindowedProduct. */
    public static class ProductProcessWindowFunction
            extends ProcessWindowFunction<
                    Tuple2<Long, Double>, WindowedProduct, String, TimeWindow> {

        private static final long serialVersionUID = 1L;
        private static final int EVENT_TIME_LAG_WINDOW_SIZE = 10_000;

        private transient DescriptiveStatisticsHistogram eventTimeLag;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            eventTimeLag =
                    getRuntimeContext()
                            .getMetricGroup()
                            .histogram(
                                    "eventTimeLag",
                                    new DescriptiveStatisticsHistogram(EVENT_TIME_LAG_WINDOW_SIZE));
        }

        @Override
        public void process(
                final String product_name,
                final Context context,
                final Iterable<Tuple2<Long, Double>> input,
                final Collector<WindowedProduct> out) {

            WindowedProduct windowedProduct = new WindowedProduct();
            Tuple2<Long, Double> aggregated = input.iterator().next();

            windowedProduct.setEventsPerWindow(aggregated.f0);
            windowedProduct.setSumPerWindow(aggregated.f1);

            final TimeWindow window = context.window();
            windowedProduct.setWindow(window);
            windowedProduct.setProduct_name(product_name);

            eventTimeLag.update(System.currentTimeMillis() - window.getEnd());
            out.collect(windowedProduct);
        }
    }
}
