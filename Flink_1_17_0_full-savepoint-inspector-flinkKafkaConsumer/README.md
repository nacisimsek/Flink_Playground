# Flink 1.17.0 Savepoint Inspector with FlinkKafkaConsumer

## Overview
A utility project for inspecting Flink savepoints created with FlinkKafkaConsumer. This tool helps analyze savepoint contents and state information for troubleshooting and migration purposes.

## Features
- **Flink Version**: 1.17.0
- **Java Version**: 11
- **Savepoint Analysis**: Comprehensive savepoint inspection
- **State Processing**: State content examination
- **Kafka State**: FlinkKafkaConsumer state analysis

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/inspector/
│   │       └── SavepointInspector.java
│   └── resources/
output/                           # Savepoint analysis results
pom.xml                          # Maven configuration
```

## Key Dependencies
- Apache Flink 1.17.0 (state-processor-api, core, runtime, clients)
- Flink Kafka Connector 1.17.0
- Java FileSystem APIs

## What This Code Does
- Reads and analyzes Flink savepoint files
- Extracts operator state information from savepoints
- Inspects FlinkKafkaConsumer offsets and metadata
- Provides detailed analysis of checkpoint/savepoint structure
- Helps with debugging state-related issues in Kafka-based Flink jobs
- Facilitates savepoint migration and troubleshooting

## Key Components
- **SavepointInspector**: Main inspection utility class
- **State Processor API**: For reading savepoint data
- **FileSystem Integration**: Savepoint file access and analysis
- **Kafka Consumer State Analysis**: Specific tools for Kafka offset inspection

## Build & Run
```bash
# Build the project
mvn clean package

# Run savepoint inspection
java -jar target/full-savepoint-inspector-1_17-1.0-SNAPSHOT.jar [savepoint-path]
```

## Usage
This tool analyzes savepoints created by Flink jobs using FlinkKafkaConsumer and provides detailed information about:
- Operator states and their contents
- Kafka consumer offsets and partition assignments
- Checkpoint metadata and configuration
- State backend information and serialization details
- Operator chain information and parallelism settings

## Use Cases
- Debugging Flink job state issues
- Analyzing savepoint contents before migration
- Troubleshooting Kafka consumer offset problems
- Understanding state structure for optimization
- Savepoint validation and verification

## Configuration
- **Savepoint Path**: Provided as command line argument
- **Output Format**: Detailed console logging and file output
- **Analysis Depth**: Configurable inspection levels