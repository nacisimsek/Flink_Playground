# Flink 1.17.0 Migration: FlinkKafkaConsumer

## Overview
This project demonstrates migration scenarios for FlinkKafkaConsumer in Flink 1.17.0. It provides examples and utilities for migrating from legacy FlinkKafkaConsumer to the new KafkaSource API.

## Features
- **Flink Version**: 1.17.0
- **Java Version**: 8 (compatible)
- **Migration Focus**: FlinkKafkaConsumer to KafkaSource transition
- **Compatibility**: Legacy consumer support
- **Migration Tools**: Utilities for smooth transition

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Migration Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.17.0 (streaming, clients, runtime)
- Flink Kafka Connector (legacy FlinkKafkaConsumer)
- Log4j 2.17.1
- Kafka Clients API

## What This Code Does
- Demonstrates usage of deprecated FlinkKafkaConsumer in Flink 1.17.0
- Provides migration examples from FlinkKafkaConsumer to KafkaSource
- Shows compatibility patterns for legacy Kafka consumer code
- Illustrates state migration techniques for Kafka consumers
- Helps with smooth transition from old to new Kafka connector APIs
- Provides testing utilities for migration validation

## Migration Scenarios Covered
- **State Compatibility**: Preserving consumer offsets during migration
- **Configuration Mapping**: Converting FlinkKafkaConsumer configs to KafkaSource
- **Serialization Handling**: Maintaining data serialization compatibility
- **Failure Recovery**: Ensuring checkpoint compatibility during migration

## Build & Run
```bash
# Build the project
mvn clean package

# Run migration example
java -jar target/Flink_1_17_0_Migration_FlinkKafkaConsumer-1.5.jar
```

## Use Cases
- Legacy system migration to newer Flink versions
- Gradual transition from FlinkKafkaConsumer to KafkaSource
- Testing migration compatibility
- Understanding deprecated API usage patterns
- Savepoint migration between connector versions

## Migration Benefits
- **Improved Performance**: Better throughput with new KafkaSource
- **Enhanced Features**: Advanced watermarking and event-time processing
- **Better Fault Tolerance**: Improved recovery mechanisms
- **Modern API**: Cleaner and more intuitive configuration

## Configuration
- **Legacy Consumer Properties**: FlinkKafkaConsumer configuration
- **Migration Settings**: Transition-specific parameters
- **State Backend**: Compatible state management settings