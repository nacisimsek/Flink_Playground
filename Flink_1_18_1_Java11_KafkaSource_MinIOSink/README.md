# Flink 1.18.1 Java11: KafkaSource to MinIO Sink

## Overview
This project implements a high-performance streaming pipeline that consumes data from Kafka using the modern KafkaSource connector and writes processed data directly to MinIO object storage, optimized for data lake architectures.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Source**: KafkaSource (modern Kafka connector)
- **Sink**: MinIO (S3-compatible object storage)
- **Performance**: Optimized for high-throughput streaming
- **Reliability**: Exactly-once processing guarantees

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── KafkaSource-MinIO Pipeline Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
MinIO_output/                     # MinIO output directory
pom.xml                          # Maven configuration
```

## Key Dependencies
- Apache Flink 1.18.1 (streaming, clients, runtime)
- Flink Kafka Connector (KafkaSource API)
- MinIO Java SDK
- S3 FileSystem connector
- Log4j 2.17.1

## What This Code Does
- **Real-time Ingestion**: Consumes streaming data from Kafka topics
- **Data Transformation**: Applies real-time transformations and enrichments
- **Object Storage**: Writes data directly to MinIO buckets with optimized formats
- **Partitioning**: Implements time-based and content-based partitioning
- **Compression**: Applies compression for storage efficiency
- **Monitoring**: Provides comprehensive metrics and observability

## Pipeline Architecture
- **KafkaSource Configuration**: Modern connector with enhanced performance
- **Stream Processing**: Real-time data transformations
- **MinIO Sink**: Direct writing to object storage with batching
- **Checkpointing**: Fault-tolerant state management
- **Watermarking**: Event-time processing support

## Build & Run
```bash
# Build the project
mvn clean package

# Run the pipeline
java -jar target/Flink_1_18_1_Java11_KafkaSource_MinIOSink-1.0.jar
```

## Data Flow Features
- **Streaming ETL**: Extract, transform, load in real-time
- **Format Support**: JSON, Parquet, Avro output formats
- **Schema Registry**: Schema evolution and compatibility
- **Data Quality**: Built-in validation and error handling
- **Scalability**: Horizontal scaling support

## Use Cases
- **Data Lake Ingestion**: Real-time data lake population
- **Analytics Pipeline**: Feeding downstream analytics systems
- **Data Archival**: Long-term storage with efficient formats
- **Stream Processing**: Real-time data transformation and enrichment
- **Event Sourcing**: Event storage for event-driven architectures

## Performance Optimizations
- **Batch Writing**: Optimized batch sizes for MinIO
- **Connection Pooling**: Efficient MinIO connection management
- **Memory Management**: Optimized for large-scale data processing
- **Parallel Processing**: Multi-threaded processing capabilities

## Configuration
- **Kafka Configuration**: Topics, consumer groups, serialization
- **MinIO Configuration**: Endpoints, buckets, access credentials
- **Processing Configuration**: Parallelism, checkpointing intervals
- **Output Configuration**: File formats, compression, partitioning