package naci.grpId;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.table.functions.ScalarFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Flink UDF that attempts to parse a JSON string.
 * If parsing fails, it logs the malformed JSON and returns null.
 */
public class jsonUdf extends ScalarFunction {

    private static final Logger LOG = LoggerFactory.getLogger(jsonUdf.class);
    // ObjectMapper is thread-safe after configuration so it can be static
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Parses the input JSON string. If the JSON is well-formed, returns a string representation
     * of the parsed JSON (or you can return a specific field or transformed value).
     * If the JSON is malformed, logs a warning and returns null.
     *
     * @param jsonStr the JSON string to parse
     * @return a string representation of the parsed JSON, or null if parsing fails
     */
    public String eval(String jsonStr) {
        if (jsonStr == null) {
            return null;
        }
        try {
            JsonNode node = MAPPER.readTree(jsonStr);
            // Here you might want to extract a particular field or simply return the original JSON.
            return node.toString();
        } catch (Exception e) {
            // Log the malformed JSON along with the exception details.
            LOG.warn("Malformed JSON encountered: {}", jsonStr, e);
            // Return null to indicate that parsing failed,
            // so downstream processing can decide how to handle it.
            return null;
        }
    }
}
