package naci.grpId;


import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import java.util.List;

public class Main {
    public static void main(String[] args) throws SqlParseException {
        // SQL query
        String sqlQuery = "SELECT field1, field2 FROM my_table WHERE field1 > 10";

        // Parse the SQL query
        SqlParser parser = SqlParser.create(sqlQuery);
        SqlNode sqlNode = parser.parseStmt();

        // Extract information from the parsed SQL query
        if (sqlNode instanceof SqlSelect) {
            SqlSelect select = (SqlSelect) sqlNode;
            // Extract fields
            List<SqlNode> selectList = select.getSelectList().getList();
            for (SqlNode node : selectList) {
                System.out.println("Field: " + node.toString());
            }
            // Extract table name
            SqlNode from = select.getFrom();
            System.out.println("Table: " + from.toString());
            // Extract conditions
            SqlNode where = select.getWhere();
            if (where != null) {
                System.out.println("Condition: " + where.toString());
            }
        }
    }
}