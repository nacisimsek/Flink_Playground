package naci.grpId;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class KafkaAggregationJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(10);

        KafkaSource<AggregationEvent> source = KafkaSource.<AggregationEvent>builder()
                .setBootstrapServers("35.156.211.158:9092")
                .setGroupId("flink-aggregation-group")
                .setTopics("dss25_p10_01")
                .setStartingOffsets(OffsetsInitializer.committedOffsets())
                .setValueOnlyDeserializer(new AggregationEventDeserializationSchema())
                .build();

        // INTENTIONALLY USING SAME UID "kafka-source" TO TRIGGER OPERATOR STATE INFLATION
        DataStream<AggregationEvent> input = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .uid("kafka-source");

        input
                .keyBy(event -> event.id)
                .process(new KeyedProcessFunction<String, AggregationEvent, String>() {
                    private transient ValueState<Long> countState;
                    private transient ValueState<Double> sumState;

                    @Override
                    public void open(org.apache.flink.configuration.Configuration parameters) {
                        countState = getRuntimeContext().getState(new ValueStateDescriptor<>("count", Long.class));
                        sumState = getRuntimeContext().getState(new ValueStateDescriptor<>("sum", Double.class));
                    }

                    @Override
                    public void processElement(AggregationEvent event, Context ctx, Collector<String> out) throws Exception {
                        long currentCount = countState.value() == null ? 0L : countState.value();
                        double currentSum = sumState.value() == null ? 0.0 : sumState.value();

                        currentCount++;
                        currentSum += event.value;

                        countState.update(currentCount);
                        sumState.update(currentSum);

                        out.collect("Key: " + event.id + " | Count: " + currentCount + " | Sum: " + currentSum);
                    }
                })
                .uid("uid-aggregation")
                .name("Aggregation Logic")
                .print()
                .uid("uid-print");

        env.execute("Kafka Aggregation Flink Job (with KafkaSource)");
    }
}
