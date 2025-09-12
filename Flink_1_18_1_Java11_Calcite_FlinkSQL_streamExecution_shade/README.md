# Flink 1.18.1 Java11 Calcite FlinkSQL Stream Execution (Shaded)

## Overview
This project demonstrates SQL parsing using Apache Calcite within a Flink 1.18.1 streaming application. This version includes shaded Calcite dependencies to avoid conflicts.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Calcite Integration**: SQL parsing and analysis
- **Stream Processing**: Real-time SQL query parsing
- **Shaded Dependencies**: Relocated Calcite packages to avoid conflicts

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main.java          # Main application with streaming SQL parser
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                            # Maven configuration with shading
```

## Key Components
- **StreamExecutionEnvironment**: Flink streaming context
- **SqlParser**: Calcite SQL parser for query analysis
- **RichFlatMapFunction**: Custom function for SQL parsing logic
- **Maven Shade Plugin**: Relocates Calcite packages to `naci.grpId.shaded.calcite`

## Dependencies
- Apache Flink 1.18.1 (streaming, table, clients)
- Apache Calcite (shaded)
- Log4j 2.17.1

## Build & Run
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/Flink_1_18_1_Java11_Calcite_SQLParser-1.0.jar
```

## Usage
The application parses a sample SQL query and extracts:
- Field names from SELECT clause
- Table name from FROM clause  
- Conditions from WHERE clause

## Configuration
- **Main Class**: `naci.grpId.Main`
- **Shaded Pattern**: `org.apache.calcite` → `naci.grpId.shaded.calcite`
- **Target Java**: 11