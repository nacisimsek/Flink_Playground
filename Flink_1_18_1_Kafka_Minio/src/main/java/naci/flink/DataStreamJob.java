package naci.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

import java.util.concurrent.TimeUnit;

public class DataStreamJob {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// Define Kafka Source
		KafkaSource<String> source = KafkaSource.<String>builder()
				.setBootstrapServers("kafka:9092")
				.setTopics("dbserver4.public.customers1")
				.setGroupId("flink_group")
				.setStartingOffsets(org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new SimpleStringSchema())
				.build();

		// Add Kafka source to the environment
		var kafkaStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

		// Define FileSink (MinIO Sink) for writing JSON strings to CSV format
		var sink = FileSink
				.forRowFormat(new Path("s3a://dataset/naci"), new SimpleStringSchema())
				.withRollingPolicy(OnCheckpointRollingPolicy.build())
				.build();

		// Apply sink to the stream
		kafkaStream.sinkTo(sink);

		// Execute the Flink job
		env.execute("Flink Java API - Kafka to MinIO");
	}
}
