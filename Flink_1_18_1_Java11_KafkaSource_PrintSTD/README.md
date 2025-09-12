# Flink 1.18.1 Java11: KafkaSource to Print Standard Output

## Overview
This project demonstrates a simple yet powerful Flink 1.18.1 streaming application that consumes messages from Kafka using the modern KafkaSource connector and outputs them to standard output for monitoring and debugging purposes.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Source**: KafkaSource (modern Kafka connector)
- **Output**: Standard output/console printing
- **Debugging**: Real-time message monitoring
- **Simplicity**: Clean, minimal implementation

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── KafkaSource Print Implementation
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.18.1 (streaming, clients, runtime)
- Flink Kafka Connector (modern KafkaSource)
- Log4j 2.17.1
- Kafka Clients API

## What This Code Does
- **Real-time Consumption**: Consumes messages from Kafka topics using KafkaSource
- **Message Display**: Prints incoming messages to console in real-time
- **Format Handling**: Processes various message formats (JSON, String, Avro)
- **Debug Information**: Shows message metadata (timestamps, partitions, offsets)
- **Monitoring**: Provides simple monitoring of Kafka topic activity
- **Development Support**: Ideal for development and testing scenarios

## Key Features
- **Modern KafkaSource**: Uses the latest Kafka connector API
- **Real-time Output**: Immediate message display with minimal latency
- **Message Metadata**: Displays partition, offset, and timestamp information
- **Error Handling**: Graceful handling of deserialization errors
- **Clean Logging**: Well-formatted console output for easy reading

## Build & Run
```bash
# Build the project
mvn clean package

# Run the Kafka consumer with console output
java -jar target/Flink_1_18_1_Java11_KafkaSource_PrintSTD-1.0.jar
```

## Output Format Examples
```
Message from partition 0, offset 12345: {"userId": 123, "action": "click"}
Message from partition 1, offset 67890: {"userId": 456, "action": "view"}
Timestamp: 2024-09-12T10:30:00Z, Topic: user-events
```

## Use Cases
- **Development Testing**: Quick testing of Kafka producers and topics
- **Message Monitoring**: Real-time monitoring of message flow
- **Debugging**: Troubleshooting Kafka connectivity and data issues
- **Data Validation**: Verifying message formats and content
- **Learning**: Understanding Kafka message structures and flow
- **Integration Testing**: Validating end-to-end message delivery

## Configuration Options
- **Kafka Bootstrap Servers**: Configurable cluster connection
- **Topic Subscription**: Single or multiple topic consumption
- **Consumer Group**: Configurable consumer group assignment
- **Deserialization**: Support for various data formats
- **Offset Management**: Configurable offset reset strategies

## Monitoring Features
- **Message Count**: Track number of processed messages
- **Throughput Metrics**: Messages per second statistics
- **Lag Monitoring**: Consumer lag information
- **Error Tracking**: Failed message processing statistics
- **Performance Metrics**: Processing latency measurements

## Development Benefits
- **Quick Setup**: Minimal configuration required
- **Visual Feedback**: Immediate visual confirmation of message flow
- **Debug Information**: Rich metadata for troubleshooting
- **Flexible Configuration**: Easy adaptation for different environments