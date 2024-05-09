package naci.grpId;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.Properties;
import java.util.Random;

public class SecureKafkaProducerJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> randomJsonDataStream = env.addSource(new SourceFunction<String>() {
            private volatile boolean isRunning = true;
            private final Random random = new Random();
            private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                while (isRunning) {
                    ObjectNode jsonObject = nodeFactory.objectNode();
                    jsonObject.put("id1", random.nextInt(10));
                    jsonObject.put("id2", random.nextInt(100));
                    jsonObject.put("id3", random.nextInt(1000));
                    ctx.collect(jsonObject.toString());
                    Thread.sleep(1000); // Emit one event per second
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "kafka-controller-0.kafka-controller-headless.vvp.svc.cluster.local:9092,kafka-controller-1.kafka-controller-headless.vvp.svc.cluster.local:9092,kafka-controller-2.kafka-controller-headless.vvp.svc.cluster.local:9092");
        properties.setProperty("security.protocol", "SASL_PLAINTEXT");
        properties.setProperty("sasl.mechanism", "SCRAM-SHA-256");
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"user1\" password=\"BTDdxMnawb\";");

        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                .setBootstrapServers(properties.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(properties)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("test")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build())
                .build();

        randomJsonDataStream.sinkTo(kafkaSink);

        env.execute("Secure Kafka Producer Job");
    }
}
