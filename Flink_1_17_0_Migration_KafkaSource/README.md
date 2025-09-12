# Flink 1.17.0 Migration: Modern KafkaSource

## Overview
This project demonstrates the implementation of the modern KafkaSource API in Flink 1.17.0, showcasing best practices for new Kafka connector usage and migration from legacy FlinkKafkaConsumer.

## Features
- **Flink Version**: 1.17.0
- **Java Version**: 8 (compatible)
- **Modern API**: New KafkaSource connector implementation
- **Enhanced Performance**: Improved throughput and resource utilization
- **Advanced Features**: Better watermarking and event-time processing

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── KafkaSource Implementation Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.17.0 (streaming, clients, runtime)
- Flink Kafka Connector (new KafkaSource API)
- Log4j 2.17.1
- Kafka Clients API

## What This Code Does
- Implements the modern KafkaSource API for Kafka consumption
- Demonstrates advanced KafkaSource configuration options
- Shows proper watermark and event-time handling with KafkaSource
- Provides examples of bounded and unbounded Kafka reading
- Illustrates best practices for KafkaSource performance tuning
- Demonstrates proper resource management and fault tolerance

## KafkaSource Advantages
- **Better Performance**: Improved throughput compared to FlinkKafkaConsumer
- **Enhanced Watermarking**: More accurate event-time processing
- **Flexible Boundaries**: Support for bounded and unbounded reading
- **Better Resource Management**: Optimized resource utilization
- **Improved Fault Tolerance**: Enhanced recovery mechanisms

## Build & Run
```bash
# Build the project
mvn clean package

# Run KafkaSource example
java -jar target/Flink_1_17_0_Migration_KafkaSource-1.5.jar
```

## Key Features Demonstrated
- **KafkaSource Configuration**: Proper setup and configuration
- **Watermark Strategies**: Custom watermark generation for event-time processing
- **Deserialization**: Flexible data deserialization options
- **Offset Management**: Advanced offset handling and recovery
- **Partitioning**: Partition-aware processing and scaling

## Use Cases
- Modern stream processing applications
- High-throughput Kafka consumption
- Event-time processing with accurate watermarks
- Large-scale data streaming pipelines
- Real-time analytics with Kafka integration

## Migration Benefits from FlinkKafkaConsumer
- **Performance Improvements**: Up to 2x better throughput
- **Better Watermarking**: More accurate event-time processing
- **Enhanced Monitoring**: Better metrics and observability
- **Flexible Reading**: Support for both bounded and unbounded streams

## Configuration
- **Kafka Bootstrap Servers**: Cluster connection configuration
- **Topic Subscriptions**: Dynamic topic discovery and assignment
- **Consumer Properties**: Advanced consumer behavior tuning
- **Watermark Strategy**: Event-time processing configuration