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
//import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.lablatency.event.Product;
import com.ververica.lablatency.event.ProductRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Map;

import static org.apache.flink.api.java.typeutils.TypeExtractor.getForClass;

/** SortingJob with coalesced timers. */
public class SortingJobCoalescedTimer {
    private static final Logger LOG = LoggerFactory.getLogger(SortingJobCoalescedTimer.class);

    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromArgs(args);
        LOG.info("params: " + params.getProperties());

        Configuration config = GlobalConfiguration.loadConfiguration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        final String jobName =
                params.get("job-name", SortingJobCoalescedTimer.class.getSimpleName());
        final String kafkaAddress = params.get("kafka", "localhost:9093");
        final String topic = params.get("topic", "lablatency");
        final String group = params.get("group", "lablatency");

        final int outOfOrderness = params.getInt("out-of-orderness", 250);
        final int roundTimerTo = params.getInt("round-timer-to", 100);

        final boolean useOneMapper = params.getBoolean("use-one-mapper", true);
        final boolean forceKryo = params.getBoolean("force-kryo", false);

        // Note: enableForceKryo() is deprecated in Flink 1.20+
        if (forceKryo) {
            LOG.warn("force-kryo option is deprecated and ignored in Flink 1.20+");
        }

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
                        .flatMap(
                                useOneMapper
                                        ? new ProductDeserializerOneGlobalMapper()
                                        : new ProductDeserializerOneMapperPerEvent())
                        .name("Deserialization")
                        .uid("Deserialization")
                        .keyBy(record -> record.f0.getProductId())
                        .process(new SortFunction(roundTimerTo))
                        .name("Sort")
                        .uid("Sort")
                        .map(new MapProduct())
                        .name("MapProduct")
                        .uid("MapProduct");

        // Create a separate stream for moving average processing
        sourceStream
                .keyBy(record -> record.f0.getProductId())
                .process(new MovingAverageProducts())
                .name("MovingAverage")
                .uid("MovingAverage")
                .print("Moving Average")
                .name("MovingAverageOutput")
                .uid("MovingAverageOutput");

        sourceStream
                .print("Products")
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

    /** Deserializes ProductRecord into Product: create one ObjectMapper per event */
    public static class ProductDeserializerOneMapperPerEvent
            extends RichFlatMapFunction<ProductRecord, Tuple2<Product, Long>> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOG =
                LoggerFactory.getLogger(ProductDeserializerOneMapperPerEvent.class);

        @Override
        public void flatMap(
                final ProductRecord kafkaRecord,
                final Collector<Tuple2<Product, Long>> out) {
            final ObjectMapper objectMapper = new ObjectMapper();
            final Product product;
            try {
                product = objectMapper.readValue(kafkaRecord.getValue(), Product.class);
            } catch (IOException e) {
                LOG.error("Failed to deserialize: " + e.getLocalizedMessage());
                return;
            }
            out.collect(new Tuple2<>(product, kafkaRecord.getTimestamp()));
        }
    }

    /**
     * Deserializes ProductRecord into Product: create one global ObjectMapper per operator
     * instance
     */
    public static class ProductDeserializerOneGlobalMapper
            extends RichFlatMapFunction<ProductRecord, Tuple2<Product, Long>> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOG =
                LoggerFactory.getLogger(ProductDeserializerOneGlobalMapper.class);

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
                product = this.objectMapper.readValue(kafkaRecord.getValue(), Product.class);
            } catch (IOException e) {
                LOG.error("Failed to deserialize: " + e.getLocalizedMessage());
                return;
            }
            out.collect(new Tuple2<>(product, kafkaRecord.getTimestamp()));
        }
    }

    private static class ProductByTimeComparator
            implements Comparator<Tuple2<Product, Long>> {

        @Override
        public int compare(Tuple2<Product, Long> o1, Tuple2<Product, Long> o2) {
            return Long.compare(o1.f1, o2.f1);
        }
    }

    /** SortFunction with timer coalescing: round timer to {@code roundTo} */
    public static class SortFunction
            extends KeyedProcessFunction<
                    Integer, Tuple2<Product, Long>, Tuple2<Product, Long>> {

        private ListState<Tuple2<Product, Long>> listState;

        private final int roundTo;

        public SortFunction(int roundTo) {
            this.roundTo = roundTo;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);

            ListStateDescriptor<Tuple2<Product, Long>> desc =
                    new ListStateDescriptor<>(
                            "events",
                            TypeInformation.of(new TypeHint<Tuple2<Product, Long>>() {}));
            listState = getRuntimeContext().getListState(desc);
        }

        @Override
        public void processElement(
                Tuple2<Product, Long> value,
                Context ctx,
                Collector<Tuple2<Product, Long>> out)
                throws Exception {

            TimerService timerService = ctx.timerService();
            long currentTimestamp = ctx.timestamp();

            if (currentTimestamp > timerService.currentWatermark()) {
                listState.add(value);
                long roundedTimestamp = ((currentTimestamp + roundTo) / roundTo) * roundTo;
                timerService.registerEventTimeTimer(roundedTimestamp);
            }
        }

        @Override
        public void onTimer(
                long timestamp, OnTimerContext ctx, Collector<Tuple2<Product, Long>> out)
                throws Exception {

            ArrayList<Tuple2<Product, Long>> list = new ArrayList<>();
            ArrayList<Tuple2<Product, Long>> emit = new ArrayList<>();

            listState
                    .get()
                    .forEach(
                            event -> {
                                long roundedTimestamp = ((event.f1 + roundTo) / roundTo) * roundTo;
                                if (roundedTimestamp == timestamp) {
                                    emit.add(event);
                                } else {
                                    list.add(event);
                                }
                            });

            emit.sort(new ProductByTimeComparator());
            emit.forEach(out::collect);

            listState.update(list);
        }
    }

    /** This class is used to calculate eventTimeLag after the second watermark are added */
    public static class MapProduct
            extends RichMapFunction<Tuple2<Product, Long>, Tuple2<Product, Long>> {

        private transient DescriptiveStatisticsHistogram eventTimeLag;
        private static final int EVENT_TIME_LAG_WINDOW_SIZE = 10_000;

        @Override
        public void open(final Configuration parameters) throws Exception {
            eventTimeLag =
                    getRuntimeContext()
                            .getMetricGroup()
                            .histogram(
                                    "eventTimeLag",
                                    new DescriptiveStatisticsHistogram(EVENT_TIME_LAG_WINDOW_SIZE));
        }

        @Override
        public Tuple2<Product, Long> map(Tuple2<Product, Long> value) throws Exception {
            eventTimeLag.update(System.currentTimeMillis() - value.f1);
            return value;
        }
    }

    /**
     * Implements an exponentially moving average with a coefficient of 0.5
     */
    private static class MovingAverageProducts
            extends KeyedProcessFunction<
                    Integer, Tuple2<Product, Long>, Tuple3<Integer, Double, Long>> {

        private ValueState<Double> movingAverage;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            movingAverage =
                    getRuntimeContext()
                            .getState(new ValueStateDescriptor<>("movingAverage", Types.DOUBLE));
        }

        @Override
        public void processElement(
                Tuple2<Product, Long> value,
                Context ctx,
                Collector<Tuple3<Integer, Double, Long>> out)
                throws Exception {

            Double last = movingAverage.value();
            if (last != null) {
                last = (last + value.f0.getPrice()) / 2.0;
                movingAverage.update(last);

                out.collect(Tuple3.of(ctx.getCurrentKey(), last, ctx.timestamp()));
            } else {
                movingAverage.update(value.f0.getPrice());
            }
        }
    }
}
