# Flink 1.17.1 Python: Kafka Source to MinIO Sink with Table API

## Overview
This project demonstrates a PyFlink (Python Flink) 1.17.1 streaming job that consumes JSON data from Kafka, processes it using the Table API, and writes the results to MinIO object storage. Includes Docker containerization for easy deployment.

## Features
- **Flink Version**: 1.17.1 (PyFlink)
- **Language**: Python
- **Source**: Kafka (JSON messages)
- **Sink**: MinIO (S3-compatible object storage)
- **API**: Table API for SQL-like operations
- **Containerization**: Docker and Docker Compose setup

## Project Structure
```
├── my_flink_job.py              # Main PyFlink application
├── docker-compose.yaml         # Complete infrastructure setup
├── dockerfile                  # Custom PyFlink image
├── README.txt                  # Additional documentation
└── minio_files/                # MinIO configuration and test files
```

## Key Dependencies & Infrastructure
- **PyFlink 1.17.1**: Python API for Apache Flink
- **Kafka**: Message streaming platform
- **MinIO**: S3-compatible object storage
- **Elasticsearch**: Search and analytics engine (included in docker-compose)
- **Kibana**: Data visualization platform
- **Docker**: Containerization platform

## What This Code Does
- **Data Ingestion**: Consumes JSON messages from Kafka topics
- **Schema Definition**: Defines complex nested JSON schema for Kafka data
- **Data Transformation**: Processes data using Flink Table API and SQL operations
- **Object Storage**: Writes processed data to MinIO buckets in various formats
- **Real-time Processing**: Handles streaming data with configurable checkpointing
- **Infrastructure Management**: Provides complete dockerized environment

## Docker Services Included
- **Kafka & Zookeeper**: Message streaming infrastructure
- **MinIO**: Object storage with web console
- **Elasticsearch & Kibana**: Analytics and visualization stack
- **Flink JobManager & TaskManager**: Flink cluster components
- **Custom PyFlink Image**: Optimized Python Flink runtime

## Build & Run
```bash
# Start the complete infrastructure
docker-compose up -d

# Access Flink Web UI
http://localhost:8081

# Access MinIO Console
http://localhost:9001

# Access Kibana Dashboard
http://localhost:5601

# Submit PyFlink job
docker exec -it flink-jobmanager python /opt/my_flink_job.py
```

## Data Flow
1. **Source**: JSON messages from Kafka topics
2. **Processing**: Real-time transformation using Table API
3. **Schema Handling**: Complex nested JSON schema processing
4. **Sink**: Structured data storage in MinIO buckets
5. **Analytics**: Optional integration with Elasticsearch for search capabilities

## Schema Features
- **Nested JSON Support**: Handles complex JSON structures
- **Change Data Capture**: Processes CDC (Change Data Capture) patterns
- **Before/After States**: Supports database change log formats
- **Customer Data Processing**: Example with customer information schema

## Use Cases
- **CDC Processing**: Change data capture from databases to object storage
- **Data Lake Ingestion**: Stream processing to data lake (MinIO)
- **Real-time Analytics**: Processing streaming data for analytics
- **Data Pipeline**: ETL operations from Kafka to object storage
- **Microservices Integration**: Event-driven architecture support

## Configuration Features
- **Checkpointing**: Configurable fault tolerance (10-second intervals)
- **Parallelism**: Scalable processing configuration
- **Format Support**: Multiple data format options (JSON, Parquet, etc.)
- **Connector Configuration**: Flexible Kafka and MinIO settings