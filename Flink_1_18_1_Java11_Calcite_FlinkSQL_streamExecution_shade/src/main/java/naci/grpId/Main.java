package naci.grpId;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // Create the StreamExecutionEnvironment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a DataStream with a single dummy element
        env.fromElements("dummy")
                .flatMap(new RichFlatMapFunction<String, String>() {

                    private String sqlQuery;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        // Initialize your SQL query here
                        sqlQuery = "SELECT field1, field2 FROM my_table WHERE field1 > 10";
                    }

                    @Override
                    public void flatMap(String value, Collector<String> out) throws Exception {
                        // Parse the SQL query
                        SqlParser parser = SqlParser.create(sqlQuery);
                        SqlNode sqlNode = parser.parseStmt();

                        // Extract information from the parsed SQL query
                        if (sqlNode instanceof SqlSelect) {
                            SqlSelect select = (SqlSelect) sqlNode;
                            // Extract fields
                            List<SqlNode> selectList = select.getSelectList().getList();
                            for (SqlNode node : selectList) {
                                out.collect("Field: " + node.toString());
                            }
                            // Extract table name
                            SqlNode from = select.getFrom();
                            out.collect("Table: " + from.toString());
                            // Extract conditions
                            SqlNode where = select.getWhere();
                            if (where != null) {
                                out.collect("Condition: " + where.toString());
                            }
                        }
                    }
                })
                .print();

        // Execute the Flink job
        env.execute("SQL Parsing Flink Job");
    }
}
