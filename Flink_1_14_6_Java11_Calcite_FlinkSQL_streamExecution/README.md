# Flink 1.14.6 Java11 Calcite FlinkSQL Stream Execution

## Overview
This project demonstrates SQL parsing using Apache Calcite within a Flink 1.14.6 streaming application. Uses the older but stable Flink 1.14.6 version.

## Features
- **Flink Version**: 1.14.6 (LTS)
- **Java Version**: 1.8 (compatible)
- **Calcite Integration**: SQL parsing and analysis
- **Stream Processing**: Real-time SQL query parsing
- **Legacy Support**: Supports older Java environments

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main.java          # Main streaming application
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                            # Maven configuration
```

## Key Components
- **StreamExecutionEnvironment**: Flink 1.14.6 streaming context
- **SqlParser**: Calcite SQL parser integration
- **RichFlatMapFunction**: Stream processing function

## Dependencies
- Apache Flink 1.14.6 (streaming-java, clients, table)
- Apache Calcite (via flink-table-planner)
- Log4j 2.17.1
- Kafka SQL Connector 3.1.0-1.18

## Build & Run
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/Flink_1_14_6_Java11_Calcite_SQLParser-1.0.jar
```

## Compatibility Notes
- Java 1.8+ compatible
- Flink 1.14.6 LTS version
- Scala 2.12 binary compatibility

## Configuration
- **Main Class**: `naci.grpId.Main`
- **Target Java**: 1.8