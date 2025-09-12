# Flink Kafka Aggregator

A Flink 1.17 streaming job that reads device events from Kafka and aggregates values by device ID.

## Project Structure

```
├── pom.xml                 # Maven configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       └── FlinkKafkaAggregator.java    # Main Flink job
│       └── resources/
│           └── log4j2.xml  # Logging configuration
└── README.md
```

## Features

- Reads JSON events from Kafka topic `topic001`
- Parses device ID and value from JSON messages
- Aggregates values by device ID using 10-second tumbling windows
- Prints aggregated results to stdout
- Fault-tolerant with checkpointing enabled

## Event Format

The job expects JSON events in the following format:
```json
{
  "id": "device-71",
  "value": 62.91
}
```

## Kafka Configuration

- **Bootstrap Servers**: `kafka:9092` (Kubernetes service name)
- **Topic**: `topic001`
- **Consumer Group**: `flink-aggregator-group`
- **Starting Offset**: earliest

## Build and Run

### Prerequisites
- Java 11 or later
- Maven 3.6+
- Apache Flink 1.17 cluster or standalone setup

### Build the Project
```bash
mvn clean package
```

### Run Locally (for development)
```bash
# Start local Flink cluster first, then submit the job
flink run target/flink-kafka-aggregator-1.0-SNAPSHOT.jar
```

### Deploy to Kubernetes
```bash
# Build the JAR
mvn clean package

# Submit to Flink cluster running in Kubernetes
kubectl exec -it <flink-jobmanager-pod> -- flink run /path/to/flink-kafka-aggregator-1.0-SNAPSHOT.jar
```

## Configuration

The job is configured to:
- Use checkpointing every 5 seconds for fault tolerance
- Process data in 10-second tumbling windows
- Connect to Kafka using the service name `kafka:9092`
- Handle malformed JSON gracefully by logging errors and using default values

## Output

Aggregated results are printed to stdout in the format:
```
(device-71,156.42)
(device-72,32.04)
...
```

Where the first element is the device ID and the second is the sum of values for that device in the current window.

## Dependencies

- Apache Flink 1.17.2
- Flink Kafka Connector 1.17.2
- Jackson for JSON processing
- Log4j2 for logging
