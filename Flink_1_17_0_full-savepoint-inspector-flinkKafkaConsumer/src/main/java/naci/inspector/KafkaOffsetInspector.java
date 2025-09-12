package naci.inspector;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.runtime.TupleSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;

public class KafkaOffsetInspector {
    public static void main(String[] args) throws Exception {
        //String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250514_01_initial_savepoint/_metadata";
        String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250515_02_bloated_savepoint/_metadata";

        //String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250515_03_fixed_kafkaSource_savepoint/_metadata";

        String kafkaSourceOperatorUid = "kafka-source";
        //String kafkaSourceOperatorUid = "kafka-source-v2";

        String kafkaOffsetStateName = "topic-partition-offset-states";
        String kafkaOffsetsOutputDir = "output/kafka_offsets_inspected_results";

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        System.out.println("Loading savepoint from: " + savepointPath);
        ExistingSavepoint savepoint = org.apache.flink.state.api.Savepoint.load(env, savepointPath);
        System.out.println("Savepoint loaded successfully.");

        System.out.println("Reading Kafka consumer offsets from operator UID: " + kafkaSourceOperatorUid);

        TupleSerializer<Tuple2<KafkaTopicPartition, Long>> kafkaOffsetSerializer = createKafkaOffsetSerializer(env.getConfig());

        DataSet<Tuple2<KafkaTopicPartition, Long>> offsets = savepoint.readUnionState(
            kafkaSourceOperatorUid,
            kafkaOffsetStateName,
            Types.TUPLE(Types.GENERIC(KafkaTopicPartition.class), Types.LONG),
            kafkaOffsetSerializer
        );

        DataSet<String> formatted = offsets.map(t ->
            String.format("Topic: %s, Partition: %d, Offset: %d", t.f0.getTopic(), t.f0.getPartition(), t.f1)
        ).returns(String.class);

        formatted.writeAsText(kafkaOffsetsOutputDir).setParallelism(1);
        formatted.print();

        System.out.println("Kafka Offset Inspector job completed.");
    }

    private static TupleSerializer<Tuple2<KafkaTopicPartition, Long>> createKafkaOffsetSerializer(ExecutionConfig config) {
        TypeSerializer<?>[] fieldSerializers = new TypeSerializer<?>[] {
            new KryoSerializer<>(KafkaTopicPartition.class, config),
            LongSerializer.INSTANCE
        };
        @SuppressWarnings("unchecked")
        Class<Tuple2<KafkaTopicPartition, Long>> tupleClass = (Class<Tuple2<KafkaTopicPartition, Long>>) (Class<?>) Tuple2.class;
        return new TupleSerializer<>(tupleClass, fieldSerializers);
    }
}