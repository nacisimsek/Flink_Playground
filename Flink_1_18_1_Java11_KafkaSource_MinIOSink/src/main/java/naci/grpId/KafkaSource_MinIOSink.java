package naci.grpId;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import java.util.concurrent.TimeUnit;

public class KafkaSource_MinIOSink {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Configuration for MinIO
/*        Configuration config = new Configuration();
        config.setString("s3.endpoint", "http://minio.vvp.svc:9000");
        config.setString("s3.access-key", "admin");
        config.setString("s3.secret-key", "password");
        config.setBoolean("s3.path.style.access", true);
        env.getConfig().setGlobalJobParameters(config);*/





        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("kafka-controller-headless.vvp.svc.cluster.local:9092")
                .setTopics("test")
                .setGroupId("flink-minio-consumer")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperty("security.protocol", "SASL_PLAINTEXT")
                .setProperty("sasl.mechanism", "SCRAM-SHA-256")
                .setProperty("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"user1\" password=\"BTDdxMnawb\";")
                .setProperty("auto.offset.reset", "latest")
                .build();


        WatermarkStrategy<String> watermarkStrategy = WatermarkStrategy
                .<String>forMonotonousTimestamps()
                .withTimestampAssigner((event, timestamp) -> System.currentTimeMillis());

        DataStream<String> stream = env.fromSource(source, watermarkStrategy, "Kafka Source");

        // Define a file sink with a very small rolling policy
        FileSink<String> sink = FileSink
                .forRowFormat(new Path("s3a://flink/"), new SimpleStringEncoder<String>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withMaxPartSize(1024) // 1 KB
                                .withRolloverInterval(TimeUnit.SECONDS.toMillis(1)) // 1 second
                                .withInactivityInterval(TimeUnit.MINUTES.toMillis(1)) // 1 minute
                                .build()
                )
                .withOutputFileConfig(OutputFileConfig.builder().withPartPrefix("prefix").withPartSuffix(".json").build())
                .build();

        stream.sinkTo(sink);

        env.execute("Kafka to MinIO Streaming Job");
    }
}
