# Flink 1.18.1 Java11: Minicluster with Flink Web UI

## Overview
This project demonstrates running Flink 1.18.1 in minicluster mode with an embedded web UI, providing a complete local Flink environment for development, testing, and learning purposes without requiring a separate Flink cluster setup.

## Features
- **Flink Version**: 1.18.1
- **Java Version**: 8/11 (compatible)
- **Execution Mode**: Minicluster (embedded local cluster)
- **Web UI**: Embedded Flink Web Dashboard
- **Development**: Complete local development environment
- **No External Dependencies**: Self-contained Flink setup

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── naci/grpId/
│   │       └── Minicluster Setup and Job Classes
│   └── resources/
│       └── log4j2.properties      # Logging configuration
pom.xml                           # Maven configuration with web UI dependencies
```

## Key Dependencies
- Apache Flink 1.18.1 (core, clients, runtime, java)
- Flink Web Dashboard (flink-runtime-web)
- Flink Table API and Planner
- Minicluster Runtime
- Log4j 2.17.1

## What This Code Does
- **Minicluster Setup**: Initializes an embedded Flink cluster with configurable resources
- **Web UI Integration**: Starts Flink Web Dashboard accessible via browser
- **Job Execution**: Runs Flink jobs within the local cluster environment
- **Resource Management**: Manages TaskManager and JobManager resources locally
- **Development Environment**: Provides complete Flink development and testing environment
- **Monitoring**: Full monitoring capabilities through web interface

## Minicluster Features
- **Local Execution**: Complete Flink cluster running locally
- **Web Dashboard**: Access to Flink Web UI at http://localhost:8081
- **Job Management**: Submit, cancel, and monitor jobs through UI
- **Metrics**: Real-time job and system metrics
- **Log Access**: View job logs and system information
- **Checkpoint Management**: Checkpoint and savepoint management

## Build & Run
```bash
# Build the project
mvn clean package

# Run minicluster with Web UI
java -jar target/Flink_1_18_1_Java11_Minicluster_FlinkWebUI-1.0.jar

# Access Flink Web UI
http://localhost:8081
```

## Web UI Features
- **Job Overview**: Running, finished, and failed jobs
- **Job Details**: Detailed execution plans and metrics
- **TaskManager Information**: Resource usage and task distribution
- **Checkpoint Statistics**: Checkpoint performance and history
- **Configuration**: Cluster configuration and settings
- **Log Viewer**: Real-time log viewing and filtering

## Development Benefits
- **Quick Testing**: Rapid job development and testing
- **Visual Debugging**: Visual job execution monitoring
- **Resource Monitoring**: CPU, memory, and network usage tracking
- **Performance Analysis**: Detailed performance metrics and profiling
- **Learning Tool**: Excellent for understanding Flink internals
- **Prototype Development**: Ideal for proof-of-concept development

## Configuration Options
- **Parallelism**: Configurable default parallelism
- **Memory Settings**: TaskManager and JobManager memory allocation
- **Network Configuration**: Network buffer and timeout settings
- **Checkpoint Configuration**: Checkpoint interval and backend settings
- **Web UI Port**: Customizable web interface port

## Use Cases
- **Local Development**: Development without external Flink cluster
- **Education**: Learning Flink concepts and job development
- **Prototyping**: Quick prototyping and testing of streaming applications
- **Integration Testing**: Testing Flink jobs in controlled environment
- **Demo Applications**: Demonstrations and proof-of-concepts
- **Performance Testing**: Local performance benchmarking

## Monitoring Capabilities
- **Real-time Metrics**: Live job performance metrics
- **Resource Utilization**: Memory and CPU usage monitoring
- **Throughput Analysis**: Message processing rates and latencies
- **Error Tracking**: Exception monitoring and error analysis
- **Watermark Progression**: Event-time processing monitoring