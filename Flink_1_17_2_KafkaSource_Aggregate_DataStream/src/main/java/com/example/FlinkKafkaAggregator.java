package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Flink job that reads events from Kafka and aggregates device values
 */
public class FlinkKafkaAggregator {

    public static void main(String[] args) throws Exception {
        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Enable checkpointing for fault tolerance
        env.enableCheckpointing(5000);

        // Configure Kafka source
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:19092") // Kafka service name in Kubernetes
                .setTopics("topic001")
                .setGroupId("flink-aggregator-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // Create the data stream from Kafka
        DataStream<String> kafkaStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // Process and aggregate the data
        DataStream<Tuple2<String, Double>> aggregatedStream = kafkaStream
                .map(new JsonParser())
                .keyBy(value -> value.f0) // Key by device ID
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10))) // 10-second tumbling window
                .reduce(new ValueAggregator());

        // Print results to stdout
        aggregatedStream.print();

        // Execute the job
        env.execute("Kafka Device Value Aggregator");
    }

    /**
     * Map function to parse JSON messages and extract device ID and value
     */
    public static class JsonParser implements MapFunction<String, Tuple2<String, Double>> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Tuple2<String, Double> map(String jsonString) throws Exception {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                String deviceId = jsonNode.get("id").asText();
                double value = jsonNode.get("value").asDouble();
                return new Tuple2<>(deviceId, value);
            } catch (Exception e) {
                System.err.println("Failed to parse JSON: " + jsonString + ", Error: " + e.getMessage());
                // Return a default tuple for malformed messages
                return new Tuple2<>("unknown", 0.0);
            }
        }
    }

    /**
     * Reduce function to aggregate values by summing them up
     */
    public static class ValueAggregator implements ReduceFunction<Tuple2<String, Double>> {
        @Override
        public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) throws Exception {
            return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
        }
    }
}
