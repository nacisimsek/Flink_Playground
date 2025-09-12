package naci.grpId;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;

import java.util.Properties;

public class KafkaAggregationJob {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(10);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "35.156.211.158:9092");
        props.setProperty("group.id", "flink-aggregation-group");

        FlinkKafkaConsumer<AggregationEvent> source = new FlinkKafkaConsumer<>(
                "dss25_p10_01",
                new AggregationEventDeserializationSchema(),
                props
        );

        source.setStartFromGroupOffsets();
        source.setCommitOffsetsOnCheckpoints(true);

        DataStream<AggregationEvent> input = env
                .addSource(source)
                .uid("kafka-source")
                .name("Kafka Source");

        input
                .keyBy(event -> event.id)
                .process(new KeyedProcessFunction<String, AggregationEvent, String>() {

                    private transient ValueState<Long> countState;
                    private transient ValueState<Double> sumState;

                    @Override
                    public void open(Configuration parameters) {
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
        env.execute("Kafka Aggregation Flink Job");
    }
}