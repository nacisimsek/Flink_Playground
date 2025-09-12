# Flink 1.18.1 Java11: Kafka to MinIO Integration

## Overview
This project demonstrates a complete streaming ETL pipeline using Flink 1.18.1, consuming data from Kafka and writing processed results to MinIO object storage. Built for modern data lake architectures with S3-compatible storage.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 8/11 (compatible)
- **Source**: Kafka (modern KafkaSource connector)
- **Sink**: MinIO (S3-compatible object storage)
- **Data Lake**: Modern data lake architecture support
- **High Performance**: Optimized for throughput and reliability

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Kafka-MinIO Integration Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.18.1 (streaming, clients, runtime)
- Flink Kafka Connector (modern KafkaSource)
- MinIO Java SDK / S3 Filesystem connector
- Log4j 2.17.1
- Jackson JSON processing

## What This Code Does
- **Stream Ingestion**: Consumes real-time data from Kafka topics using KafkaSource
- **Data Processing**: Applies transformations, filtering, and enrichment operations
- **Object Storage**: Writes processed data to MinIO buckets in optimized formats
- **Format Support**: Handles multiple data formats (JSON, Parquet, Avro, CSV)
- **Partitioning**: Implements intelligent data partitioning for optimal storage
- **Fault Tolerance**: Provides exactly-once processing guarantees with checkpointing

## Data Pipeline Features
- **Real-time Processing**: Low-latency stream processing
- **Batch Integration**: Support for micro-batch processing patterns
- **Schema Evolution**: Handles evolving data schemas gracefully
- **Compression**: Optimized data compression for storage efficiency
- **Monitoring**: Built-in metrics and monitoring integration

## Build & Run
```bash
# Build the project
mvn clean package

# Run the Kafka-MinIO pipeline
java -jar target/Flink_1_18_1_Java11_Kafka_MinIO-1.0.jar
```

## MinIO Configuration
- **Bucket Management**: Automatic bucket creation and management
- **Access Patterns**: Optimized for both streaming writes and batch reads
- **Security**: Secure access with credentials and encryption support
- **Lifecycle Management**: Data retention and archival policies
- **Performance Tuning**: Optimized for high-throughput scenarios

## Use Cases
- **Data Lake Ingestion**: Real-time data ingestion into data lakes
- **Event Streaming**: Processing event streams for analytics
- **CDC Processing**: Change data capture from databases to object storage
- **Log Aggregation**: Centralized log processing and storage
- **IoT Data Processing**: Sensor data collection and storage
- **Analytics Pipeline**: Feeding data warehouses and analytics platforms

## Performance Features
- **Parallel Processing**: Multi-threaded data processing and writing
- **Optimized Serialization**: Efficient data serialization for storage
- **Connection Pooling**: Optimized connection management to MinIO
- **Memory Management**: Efficient memory usage for large-scale processing
- **Backpressure Handling**: Intelligent flow control mechanisms

## Configuration Options
- **Kafka Settings**: Bootstrap servers, topics, consumer groups
- **MinIO Settings**: Endpoint, credentials, bucket configuration
- **Processing Settings**: Parallelism, checkpointing, watermarks
- **Format Settings**: Data formats, compression, partitioning schemes