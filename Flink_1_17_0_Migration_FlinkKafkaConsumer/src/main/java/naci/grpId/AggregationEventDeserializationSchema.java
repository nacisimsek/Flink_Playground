package naci.grpId;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.serialization.DeserializationSchema;

import java.io.IOException;

public class AggregationEventDeserializationSchema implements DeserializationSchema<AggregationEvent> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AggregationEvent deserialize(byte[] message) throws IOException {
        return mapper.readValue(message, AggregationEvent.class);
    }

    @Override
    public boolean isEndOfStream(AggregationEvent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<AggregationEvent> getProducedType() {
        return TypeInformation.of(AggregationEvent.class);
    }
}
