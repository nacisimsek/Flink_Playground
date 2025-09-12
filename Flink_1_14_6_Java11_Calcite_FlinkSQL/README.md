# Flink 1.14.6 Java11 Calcite FlinkSQL

## Overview
A basic SQL parsing implementation using Apache Calcite with Flink 1.14.6 LTS. This version provides a simple, straightforward approach to SQL analysis.

## Features
- **Flink Version**: 1.14.6 (Long Term Support)
- **Java Version**: 1.8 (backward compatible)
- **Calcite Integration**: Direct SQL parsing
- **Lightweight**: Minimal dependencies and complexity

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main.java          # Basic SQL parser
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                            # Maven configuration
```

## Key Components
- **SqlParser**: Direct Calcite parser usage
- **SqlParseException**: Error handling for malformed SQL
- **Console Application**: Simple command-line interface

## Dependencies
- Apache Flink 1.14.6 (java, streaming-java, clients, table)
- Apache Calcite (via flink-table-planner)
- Log4j 2.17.1

## Build & Run
```bash
# Compile the project
mvn clean compile

# Run the main class
mvn exec:java -Dexec.mainClass="naci.grpId.Main"

# Or build and run JAR
mvn clean package
java -cp target/Flink_1_14_6_Java11_Calcite_SQLParser-1.0.jar naci.grpId.Main
```

## Use Cases
- SQL query validation
- Query structure analysis
- Educational purposes
- Legacy system integration

## Compatibility
- Java 1.8+
- Flink 1.14.6 (stable LTS)
- Maven 3.6+