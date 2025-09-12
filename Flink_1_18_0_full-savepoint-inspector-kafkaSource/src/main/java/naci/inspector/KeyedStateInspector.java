package naci.inspector;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;


public class KeyedStateInspector {
    public static void main(String[] args) throws Exception {
        // Adjust this path to your actual savepoint
        //String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250514_01_initial_savepoint/_metadata";
        //String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250515_02_bloated_savepoint/_metadata";

        String savepointPath = "file:///Volumes/D/Toolbox/savepoint/20250515_02_bloated_savepoint/_metadata";
         String keyedStateOutputDir = "output/full_inspector_keyed_state";

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        ExistingSavepoint savepoint = org.apache.flink.state.api.Savepoint.load(env, savepointPath);

        // 1. Keyed State: uid-aggregation
        DataSet<String> keyedStateData = savepoint.readKeyedState("uid-aggregation", new KeyedStateReaderFunction<String, String>() {
            private transient ValueState<Long> countState;
            private transient ValueState<Double> sumState;

            @Override
            public void open(org.apache.flink.configuration.Configuration parameters) {
                countState = getRuntimeContext().getState(new ValueStateDescriptor<>("count", Long.class));
                sumState = getRuntimeContext().getState(new ValueStateDescriptor<>("sum", Double.class));
            }

            @Override
            public void readKey(String key, Context ctx, Collector<String> out) throws Exception {
                Long count = countState.value();
                Double sum = sumState.value();
                out.collect("Keyed State - Key: " + key + ", Count: " + count + ", Sum: " + sum);
            }
        });

        keyedStateData.writeAsText(keyedStateOutputDir).setParallelism(1);

        env.execute("Full Savepoint Inspector Job");
    }
}