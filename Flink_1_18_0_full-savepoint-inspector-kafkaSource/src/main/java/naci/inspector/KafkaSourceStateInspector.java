package naci.inspector;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.CloseableIterator;

import org.apache.flink.connector.kafka.source.split.KafkaPartitionSplit;
import org.apache.flink.connector.kafka.source.split.KafkaPartitionSplitSerializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KafkaSourceStateInspector {

    public static void main(String[] args) throws Exception {
        // === CONFIGURATION ===
        //String savepointPath = Path.of("file:///Volumes/D/Toolbox/savepoint/20250515_03_fixed_kafkaSource_savepoint/_metadata").toString();
        String savepointPath = Path.of("file:///Volumes/D/Toolbox/savepoint/20250515_02_bloated_savepoint/_metadata").toString();

       // String operatorUid = "kafka-source-v2";
        String operatorUid = "kafka-source";

        String outputDir = "output/full_inspector_kafka_source_operator_state";
        String outputFile = outputDir + "/kafka_source_offsets.txt";

        // === ENV SETUP ===
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(env, savepointPath, new HashMapStateBackend());
        DataStream<byte[]> listState = savepoint.readListState(
                OperatorIdentifier.forUid(operatorUid),
                "SourceReaderState",
                PrimitiveArrayTypeInfo.BYTE_PRIMITIVE_ARRAY_TYPE_INFO);

        CloseableIterator<byte[]> it = listState.executeAndCollect();
        KafkaPartitionSplitSerializer serializer = new KafkaPartitionSplitSerializer();

        // === CREATE OUTPUT DIR ===
        new File(outputDir).mkdirs();

        try (FileWriter writer = new FileWriter(outputFile)) {
            while (it.hasNext()) {
                byte[] full = it.next();
                KafkaPartitionSplit split = serializer.deserialize(serializer.getVersion(), Arrays.copyOfRange(full, 8, full.length));
                String stopOffset = split.getStoppingOffset().map(Object::toString).orElse("N/A");

                String line = String.format("Topic: %s | Partition: %d | Offset: %d | StopOffset: %s | TopicPartition: %s",
                        split.getTopic(),
                        split.getPartition(),
                        split.getStartingOffset(),
                        stopOffset,
                        split.getTopicPartition());

                System.out.println(line);
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Failed to write output to file: " + outputFile);
            e.printStackTrace();
        } finally {
            it.close();
        }

        System.out.println("DONE");
    }
}
