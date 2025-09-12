# 🚀 Flink Playground

A comprehensive collection of Apache Flink projects demonstrating various features, versions, and integration patterns. This repository serves as a learning playground and reference implementation for Flink streaming applications.

## 📋 Project Overview

This repository contains **23 distinct Flink projects** covering multiple Flink versions (1.14.6 to 1.19.0), integration patterns, and use cases. Each project is self-contained with its own documentation and can be built independently.

## 🗂️ Project Index

### 📊 Flink SQL & Calcite Integration
- [`Flink_1_14_6_Java11_Calcite_FlinkSQL`](./Flink_1_14_6_Java11_Calcite_FlinkSQL/) - Calcite SQL parser integration with batch execution
- [`Flink_1_14_6_Java11_Calcite_FlinkSQL_streamExecution`](./Flink_1_14_6_Java11_Calcite_FlinkSQL_streamExecution/) - Streaming execution with Calcite
- [`Flink_1_18_1_Java11_Calcite_FlinkSQL`](./Flink_1_18_1_Java11_Calcite_FlinkSQL/) - Latest Calcite integration
- [`Flink_1_18_1_Java11_Calcite_FlinkSQL_streamExecution`](./Flink_1_18_1_Java11_Calcite_FlinkSQL_streamExecution/) - Advanced streaming SQL
- [`Flink_1_18_1_Java11_Calcite_FlinkSQL_streamExecution_shade`](./Flink_1_18_1_Java11_Calcite_FlinkSQL_streamExecution_shade/) - Shaded Calcite SQL

### 🔄 Kafka Integration
- [`Flink_1_16_3_Java8_KafkaSource_PrintSTD`](./Flink_1_16_3_Java8_KafkaSource_PrintSTD/) - Basic Kafka consumer with console output
- [`Flink_1_18_1_Java11_KafkaSource_PrintSTD`](./Flink_1_18_1_Java11_KafkaSource_PrintSTD/) - Modern KafkaSource implementation
- [`Flink_1_18_1_Java11_KafkaSource_MinIOSink`](./Flink_1_18_1_Java11_KafkaSource_MinIOSink/) - Kafka to MinIO pipeline
- [`Flink_1_18_1_Java11_Randomize_KafkaSink`](./Flink_1_18_1_Java11_Randomize_KafkaSink/) - Data generation to Kafka
- [`Flink_1_18_1_Kafka_Minio`](./Flink_1_18_1_Kafka_Minio/) - Complete Kafka to MinIO solution

### 📦 Object Storage Integration
- [`Flink_1_17_2_KafkaSource_Aggregate_DataStream`](./Flink_1_17_2_KafkaSource_Aggregate_DataStream/) - Stream aggregation patterns
- [`Flink_1_18_1_Java11_Kafka_MinIO`](./Flink_1_18_1_Java11_Kafka_MinIO/) - Advanced Kafka-MinIO integration

### 🔄 Migration Examples
- [`Flink_1_17_0_Migration_FlinkKafkaConsumer`](./Flink_1_17_0_Migration_FlinkKafkaConsumer/) - Legacy FlinkKafkaConsumer
- [`Flink_1_17_0_Migration_KafkaSource`](./Flink_1_17_0_Migration_KafkaSource/) - Migration to modern KafkaSource
- [`Flink_1_17_0_Migration_KafkaSource_clean`](./Flink_1_17_0_Migration_KafkaSource_clean/) - Clean migration implementation

### 🔍 Savepoint & State Management
- [`Flink_1_17_0_full-savepoint-inspector-flinkKafkaConsumer`](./Flink_1_17_0_full-savepoint-inspector-flinkKafkaConsumer/) - Legacy savepoint inspection
- [`Flink_1_18_0_full-savepoint-inspector-kafkaSource`](./Flink_1_18_0_full-savepoint-inspector-kafkaSource/) - Modern savepoint tools
- [`Flink_1_19_0_StateProcessorAPI`](./Flink_1_19_0_StateProcessorAPI/) - Advanced state processing

### 🛠️ Development & Utilities
- [`Flink_1_18_1_Java11_Minicluster_FlinkWebUI`](./Flink_1_18_1_Java11_Minicluster_FlinkWebUI/) - Local development with Web UI
- [`Flink_1_19_0_Java8_json_udf`](./Flink_1_19_0_Java8_json_udf/) - Custom JSON UDF functions

### 🐍 Python Integration
- [`Flink_1_17_1_Python_KafkaSource_MinIOSink_TableAPI`](./Flink_1_17_1_Python_KafkaSource_MinIOSink_TableAPI/) - PyFlink with Table API

### 🔧 FlinkSQL Client Tools
- [`FlinkSQL_KafkaClient`](./FlinkSQL_KafkaClient/) - SQL client and Kafka utilities

## 🏗️ Architecture Patterns

### 📥 Data Sources
- **Kafka Integration**: Modern KafkaSource and legacy FlinkKafkaConsumer
- **Data Generation**: Random data generators for testing
- **File Sources**: CSV and JSON file processing

### 📤 Data Sinks
- **MinIO Object Storage**: S3-compatible object storage integration
- **Console Output**: Debug and development outputs
- **Kafka Sinks**: Event publishing patterns

### 🔄 Processing Patterns
- **Stream Processing**: Real-time data transformation
- **Batch Processing**: Bounded data processing
- **SQL Processing**: Declarative data processing with FlinkSQL
- **UDF Development**: Custom user-defined functions

## 🚀 Getting Started

### Prerequisites
- **Java**: Java 8 or Java 11 (project-specific)
- **Maven**: 3.6+ for building projects
- **Docker**: For containerized examples (optional)
- **Apache Flink**: Various versions from 1.14.6 to 1.19.0

### Quick Start
1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd Flink_Playground
   ```

2. **Choose a project**:
   ```bash
   cd Flink_1_18_1_Java11_KafkaSource_PrintSTD
   ```

3. **Build and run**:
   ```bash
   mvn clean package
   java -jar target/*.jar
   ```

### Project Structure
Each project follows a consistent structure:
```
Project_Name/
├── README.md          # Detailed project documentation
├── pom.xml           # Maven configuration
├── src/
│   └── main/
│       ├── java/     # Java source code
│       └── resources/ # Configuration files
└── target/           # Build output (gitignored)
```

## 📚 Learning Path

### 🎯 Beginner
1. Start with [`Flink_1_18_1_Java11_KafkaSource_PrintSTD`](./Flink_1_18_1_Java11_KafkaSource_PrintSTD/)
2. Explore [`Flink_1_18_1_Java11_Minicluster_FlinkWebUI`](./Flink_1_18_1_Java11_Minicluster_FlinkWebUI/)
3. Try [`Flink_1_18_1_Java11_Randomize_KafkaSink`](./Flink_1_18_1_Java11_Randomize_KafkaSink/)

### 🚀 Intermediate
1. Kafka Integration: [`Flink_1_18_1_Java11_KafkaSource_MinIOSink`](./Flink_1_18_1_Java11_KafkaSource_MinIOSink/)
2. SQL Processing: [`Flink_1_18_1_Java11_Calcite_FlinkSQL`](./Flink_1_18_1_Java11_Calcite_FlinkSQL/)
3. State Management: [`Flink_1_19_0_StateProcessorAPI`](./Flink_1_19_0_StateProcessorAPI/)

### 🎓 Advanced
1. Migration Patterns: Compare FlinkKafkaConsumer vs KafkaSource projects
2. UDF Development: [`Flink_1_19_0_Java8_json_udf`](./Flink_1_19_0_Java8_json_udf/)
3. PyFlink: [`Flink_1_17_1_Python_KafkaSource_MinIOSink_TableAPI`](./Flink_1_17_1_Python_KafkaSource_MinIOSink_TableAPI/)

## 🔧 Technology Stack

### Apache Flink Versions
- **Flink 1.14.6**: Stable long-term support
- **Flink 1.16.3**: Enhanced Kafka integration
- **Flink 1.17.x**: Migration and compatibility features
- **Flink 1.18.x**: Performance improvements and new APIs
- **Flink 1.19.0**: Latest features and optimizations

### Integration Technologies
- **Apache Kafka**: Stream processing source and sink
- **MinIO**: S3-compatible object storage
- **Calcite**: SQL parser and optimizer
- **Jackson**: JSON processing
- **Docker**: Containerization and orchestration

### Development Tools
- **Maven**: Build and dependency management
- **JUnit**: Unit testing framework
- **Log4j**: Logging framework
- **Flink Web UI**: Development and monitoring

## 📖 Documentation

Each project contains comprehensive documentation including:
- **Architecture Overview**: System design and data flow
- **Configuration Guide**: Setup and tuning parameters
- **Usage Examples**: Code samples and execution instructions
- **Performance Notes**: Optimization recommendations
- **Troubleshooting**: Common issues and solutions

## 🤝 Contributing

This is a personal learning playground, but contributions are welcome:
1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/new-example`
3. **Add your Flink project** with comprehensive documentation
4. **Submit pull request**

## 📄 License

This project is for educational and demonstration purposes. Each Flink project may have its own licensing considerations based on the dependencies used.

## 🔗 Useful Links

- [Apache Flink Documentation](https://flink.apache.org/docs/)
- [Flink Community](https://flink.apache.org/community.html)
- [Kafka Connector Documentation](https://nightlies.apache.org/flink/flink-docs-stable/docs/connectors/datastream/kafka/)
- [FlinkSQL Reference](https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/table/sql/)

---

**Happy Streaming with Apache Flink! 🌊**