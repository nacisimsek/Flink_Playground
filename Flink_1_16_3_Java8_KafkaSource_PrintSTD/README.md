# Flink 1.16.3 Java8 Kafka Source to Print Standard Output

## Overview
This project demonstrates a Flink 1.16.3 streaming job that consumes messages from a Kafka topic and prints them to standard output. Built with Java 8 compatibility for legacy environments.

## Features
- **Flink Version**: 1.16.3
- **Java Version**: 8 (compatible)
- **Kafka Integration**: Consumer functionality using KafkaSource
- **Output**: Standard output printing
- **Legacy Support**: Java 8 compatibility

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main Application Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
lib/                               # Kafka connector JARs
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.16.3 (streaming, clients, runtime)
- Flink Kafka Connector 1.16.3
- Log4j 2.17.1
- Kafka Clients API

## What This Code Does
- Connects to a Kafka cluster as a consumer
- Reads messages from specified Kafka topics
- Processes messages in real-time using Flink streaming
- Outputs processed messages to standard output/console
- Provides basic stream processing capabilities for monitoring and debugging

## Build & Run
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/Flink_1_16_3_Java8_KafkaSource_PrintSTD-1.17.jar
```

## Configuration
- **Kafka Bootstrap Servers**: Configurable via application parameters
- **Topic**: Specify source topic for consumption
- **Consumer Group**: Automatic assignment
- **Target Java**: 8

## Use Cases
- Real-time message monitoring
- Kafka topic debugging and inspection
- Stream processing development and testing
- Legacy system integration with Java 8 environments
- Basic ETL operations from Kafka to console output