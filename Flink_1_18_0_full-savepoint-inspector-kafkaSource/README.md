# Flink 1.18.0 Savepoint Inspector with Modern KafkaSource

## Overview
An advanced utility project for inspecting Flink savepoints created with the modern KafkaSource connector. This tool provides comprehensive analysis capabilities for Flink 1.18.0 savepoints and state management.

## Features
- **Flink Version**: 1.18.0
- **Java Version**: 11
- **Modern KafkaSource**: Supports new KafkaSource connector state analysis
- **Advanced Inspection**: Deep savepoint and state analysis capabilities
- **Enhanced Reporting**: Detailed output and analysis reports

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/inspector/
│   │       └── SavepointInspector Classes
│   └── resources/
output/                           # Savepoint analysis results and reports
pom.xml                          # Maven configuration
```

## Key Dependencies
- Apache Flink 1.18.0 (state-processor-api, core, runtime, clients)
- Flink Kafka Connector 3.2.0-1.18 (modern KafkaSource)
- Java 11 Runtime Environment

## What This Code Does
- **Modern State Analysis**: Inspects savepoints created with KafkaSource connector
- **Comprehensive Reporting**: Generates detailed analysis reports of savepoint contents
- **Offset Inspection**: Analyzes Kafka consumer offsets and partition metadata
- **State Structure Analysis**: Deep inspection of operator state hierarchies
- **Compatibility Checking**: Validates savepoint compatibility across versions
- **Performance Metrics**: Provides state size and performance analysis
- **Migration Support**: Assists with savepoint migration planning

## Advanced Features
- **KafkaSource State Parsing**: Specialized analysis for modern Kafka connector states
- **Watermark Analysis**: Inspection of watermark and event-time state
- **Operator Chain Analysis**: Detailed operator dependency and chaining inspection
- **Memory Usage Analysis**: State size and memory utilization reporting
- **Serialization Analysis**: Deep dive into state serialization formats

## Build & Run
```bash
# Build the project
mvn clean package

# Run savepoint inspection with detailed analysis
java -jar target/full-savepoint-inspector-1_18-1.0-SNAPSHOT.jar [savepoint-path] [options]

# Generate detailed report
java -jar target/full-savepoint-inspector-1_18-1.0-SNAPSHOT.jar --savepoint [path] --output [report-dir]
```

## Analysis Capabilities
- **KafkaSource Offset Analysis**: Modern connector offset and partition state
- **Watermark State Inspection**: Event-time processing state analysis
- **Checkpoint Metadata**: Comprehensive checkpoint configuration analysis
- **Operator State Details**: Per-operator state breakdown and analysis
- **Performance Metrics**: State access patterns and performance insights

## Use Cases
- **Production Debugging**: Analyzing production savepoint issues
- **Migration Planning**: Understanding state structure for version migrations
- **Performance Optimization**: Identifying state-related performance bottlenecks
- **State Validation**: Ensuring savepoint integrity and correctness
- **Capacity Planning**: Understanding state growth patterns and resource needs

## Reporting Features
- **HTML Reports**: Comprehensive web-based analysis reports
- **JSON Output**: Machine-readable analysis data
- **CSV Exports**: Tabular data for further analysis
- **Visual Charts**: State size and distribution visualizations

## Configuration Options
- **Analysis Depth**: Configurable inspection detail levels
- **Output Formats**: Multiple report format options
- **Filter Options**: Focus analysis on specific operators or state types
- **Performance Settings**: Memory and processing optimization options