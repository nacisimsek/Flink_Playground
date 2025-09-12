package naci.grpId;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        // Create a configuration object
        Configuration conf = new Configuration();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // Create a data stream of infinite integers
        DataStreamSource<Long> infiniteIntegers = env.addSource(new SourceFunction<Long>() {
            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<Long> ctx) throws Exception {
                long num = 0;
                while (isRunning) {
                    ctx.collect(num);
                    num++;
                    Thread.sleep(1003); // to slow down the number generation
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        }, "infinite-number-source");

        infiniteIntegers.print(); // Print the random integers to stdout

        env.execute("Flink Java API Skeleton");
    }
}