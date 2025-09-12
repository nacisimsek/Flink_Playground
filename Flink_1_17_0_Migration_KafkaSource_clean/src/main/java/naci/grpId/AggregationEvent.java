package naci.grpId;

public class AggregationEvent {
    public String id;
    public double value;

    public AggregationEvent() {}  // Required for POJO

    public AggregationEvent(String id, double value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AggregationEvent{id='" + id + "', value=" + value + '}';
    }
}