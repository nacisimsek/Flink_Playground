# Flink 1.18.1 Java11: Random Data Generator to Kafka Sink

## Overview
This project demonstrates a Flink 1.18.1 streaming application that generates random data and writes it to Kafka topics. Perfect for testing Kafka infrastructure, generating test data, and creating realistic data streams for development purposes.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Data Generation**: Configurable random data generation
- **Sink**: Kafka (modern KafkaSink connector)
- **Testing**: Ideal for testing and development scenarios
- **Customizable**: Flexible data schemas and generation patterns

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Random Data Generator Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration
```

## Key Dependencies
- Apache Flink 1.18.1 (streaming, clients, runtime)
- Flink Kafka Connector (modern KafkaSink)
- Jackson JSON Library (data serialization)
- Log4j 2.17.1
- Java Faker (realistic data generation)

## What This Code Does
- **Random Data Generation**: Creates realistic random data with configurable schemas
- **Streaming Production**: Continuously generates and sends data to Kafka topics
- **Format Support**: Generates data in multiple formats (JSON, Avro, String)
- **Rate Control**: Configurable data generation rates and patterns
- **Schema Flexibility**: Support for various data schemas and structures
- **Load Testing**: Generates high-volume data streams for performance testing

## Data Generation Features
- **Realistic Data**: Uses libraries like Java Faker for realistic test data
- **Custom Schemas**: Configurable data structures and field types
- **Temporal Patterns**: Time-based data generation with realistic timestamps
- **Volume Control**: Adjustable message generation rates
- **Data Variety**: Multiple data types and value distributions
- **Correlation Patterns**: Related data generation for realistic scenarios

## Build & Run
```bash
# Build the project
mvn clean package

# Run random data generator to Kafka
java -jar target/Flink_1_18_1_Java11_Randomize_KafkaSink-1.0.jar
```

## Generated Data Examples
```json
{
  "userId": 12345,
  "userName": "john.doe",
  "timestamp": "2024-09-12T10:30:00Z",
  "action": "click",
  "product": "widget-123",
  "amount": 29.99
}
```

## Configuration Options
- **Generation Rate**: Messages per second configuration
- **Data Schema**: Customizable field definitions and types
- **Kafka Topics**: Target topic configuration
- **Message Size**: Variable message size patterns
- **Value Ranges**: Min/max values for numeric fields
- **String Patterns**: Configurable string generation patterns

## Use Cases
- **Load Testing**: Generate high-volume data for Kafka performance testing
- **Development**: Create test data streams for application development
- **Integration Testing**: Generate realistic data for end-to-end testing
- **Benchmarking**: Performance benchmarking of downstream systems
- **Demo Scenarios**: Create realistic demo data for presentations
- **Stress Testing**: Test system behavior under various data load patterns

## Data Types Supported
- **User Events**: Click streams, user interactions
- **IoT Data**: Sensor readings, device telemetry
- **Financial Data**: Transactions, market data
- **E-commerce**: Orders, product views, purchases
- **Log Data**: Application logs, system events
- **Custom Formats**: Flexible schema definitions

## Performance Features
- **High Throughput**: Optimized for high-volume data generation
- **Low Latency**: Minimal overhead in data generation and sending
- **Memory Efficient**: Optimized memory usage for continuous operation
- **Scalable**: Adjustable parallelism for increased throughput
- **Monitoring**: Built-in metrics for generation rates and performance

## Advanced Features
- **Burst Patterns**: Configurable data burst generation
- **Seasonal Patterns**: Time-based data volume variations
- **Error Injection**: Controlled error generation for testing
- **Data Dependencies**: Related record generation patterns
- **Multi-Topic**: Generate data for multiple Kafka topics simultaneously