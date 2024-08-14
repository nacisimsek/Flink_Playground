package naci.grpId;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;

public class KafkaSource_Print {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        // Define the specific offsets for the partitions
        Map<TopicPartition, Long> specificOffsets = new HashMap<>();
        specificOffsets.put(new TopicPartition("topic_test", 0), 4L); // Start from offset 4 for partition 0

        KafkaSource<String> kafkaSource = KafkaSource
                .<String>builder()
                .setBootstrapServers("localhost:9093")  // Make sure the port is correct
                .setTopics("topic_test")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.offsets(specificOffsets))
                .setBounded(OffsetsInitializer.latest())
                .build();

        DataStream<String> stream = env.fromSource(
                kafkaSource,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );
        stream.print();

        env.execute("Flink KafkaSource test job");
    }
}