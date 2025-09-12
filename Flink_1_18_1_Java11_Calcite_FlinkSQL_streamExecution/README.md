# Flink 1.18.1 Java11 Calcite FlinkSQL Stream Execution

## Overview
This project demonstrates SQL parsing using Apache Calcite within a Flink 1.18.1 streaming application using standard (non-shaded) dependencies.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Calcite Integration**: SQL parsing and analysis
- **Stream Processing**: Real-time SQL query parsing
- **Standard Dependencies**: Uses original Calcite packages

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main.java          # Main application with streaming SQL parser
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                            # Maven configuration
```

## Key Components
- **StreamExecutionEnvironment**: Flink streaming context
- **SqlParser**: Calcite SQL parser for query analysis
- **RichFlatMapFunction**: Custom function for SQL parsing logic

## Dependencies
- Apache Flink 1.18.1 (streaming, table, clients, runtime)
- Apache Calcite (via flink-table-planner)
- Log4j 2.17.1
- Kafka SQL Connector 3.1.0-1.18

## Build & Run
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/Flink_1_18_1_Java11_Calcite_SQLParser-1.0.jar
```

## Usage
The application creates a streaming job that parses SQL queries and extracts structural information in real-time.

## Configuration
- **Main Class**: `naci.grpId.Main`
- **Target Java**: 11