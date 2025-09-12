# Flink 1.18.1 Java11 Calcite FlinkSQL

## Overview
A basic implementation of SQL parsing using Apache Calcite with Flink 1.18.1. This version focuses on simple SQL parsing without streaming complexity.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 11
- **Calcite Integration**: Direct SQL parsing
- **Simple Implementation**: Synchronous SQL analysis

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Main.java          # Simple SQL parser implementation
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                            # Maven configuration
```

## Key Components
- **SqlParser**: Direct Calcite SQL parser usage
- **SqlNode Analysis**: Extract SQL query components
- **Console Output**: Direct printing of parsed results

## Dependencies
- Apache Flink 1.18.1 (core, table components)
- Apache Calcite (via flink-table-planner)
- Log4j 2.17.1

## Build & Run
```bash
# Build the project
mvn clean package

# Run the application
java -cp target/Flink_1_18_1_Java11_Calcite_SQLParser-1.0.jar naci.grpId.Main
```

## Usage
The application demonstrates basic SQL parsing capabilities by analyzing a hardcoded SQL query and printing the extracted components.

## Sample Output
```
Field: `field1`
Field: `field2`
Table: `my_table`
Condition: `field1` > 10
```

## Configuration
- **Main Class**: `naci.grpId.Main`
- **Target Java**: 11