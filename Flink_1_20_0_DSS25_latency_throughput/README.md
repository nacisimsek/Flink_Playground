# Flink Latency Lab 2025

Lab to showcase different Apache Flink latency optimization techniques with product data, updated for Apache Flink 1.20.2.

## Configuration Setup

This project is optimized for local development and testing with comprehensive configuration management.

### Configuration Files

#### 1. **Workspace `.env`** (Environment Variables)
Located at project root: `.env`
```properties
FLINK_CONF_DIR=./flink-latency-25/src/main/resources
```
- **Portable relative path** - Works on any machine
- **Single source of truth** for environment configuration
- **VS Code integration** - Automatically loaded by launch configurations

#### 2. **Flink Configuration** (`src/main/resources/flink-conf.yaml`)
Comprehensive Flink cluster configuration optimized for latency testing:

**Latency Optimization Settings:**
- Network buffer timeout: `10ms` (reduced from 100ms default)
- Watermark interval: `100ms` (reduced from 200ms default)
- Latency tracking enabled with operator-level granularity

**Resource Configuration:**
- Memory settings optimized for local development
- G1GC with optimized pause times (`200ms`)
- Task slots and parallelism configured for development

**Monitoring & Metrics:**
- JMX metrics available on port `8789`
- Optional Prometheus metrics support
- Web UI available at `http://localhost:8081`

#### 3. **Logging Configuration** (`src/main/resources/log4j2.properties`)
Modern Log4j2 configuration with comprehensive component logging:
- **Timestamped console output** with Flink-style formatting
- **Explicit logger configurations** for all Flink components
- **Detailed startup logging** showing configuration loading
- **Component-specific log levels** (Flink, Kafka, Pekko, Zookeeper)

#### 4. **VS Code Integration** (`.vscode/launch.json`)
Complete run configurations for all job classes:
- **WindowingJob** - Baseline windowing with aggregation
- **IngestingJob** - Data generation and Kafka ingestion
- **EnrichingJobSync/Async** - External service enrichment
- **SortingJob** variations - Timer optimization techniques
- **WindowingJobNoAggregation** - Performance comparison baseline

### Project Structure
```
DSS25_project/                          # Workspace root
├── .env                                 # Environment variables
├── .vscode/                            
│   ├── settings.json                    # Java Maven settings
│   └── launch.json                      # All job launch configs
└── flink-latency-25/                    # Maven project
    ├── pom.xml                          # Dependencies & build config
    ├── src/main/
    │   ├── java/com/ververica/lablatency/
    │   │   ├── event/                   # Data models
    │   │   ├── job/                     # All Flink jobs
    │   │   ├── source/                  # Data generators
    │   │   └── util/                    # Utilities
    │   └── resources/
    │       ├── flink-conf.yaml          # Flink configuration
    │       ├── log4j2.properties        # Logging configuration
    │       └── product_20K.csv          # Sample data
    └── target/                          # Build outputs
```

### Dependencies & Build System

**Build System:** Maven with optimized dependency management
- **Flink 1.20.2** with all dependencies set to `provided` scope
- **Kafka Connector 3.4.0-1.20** included in JAR for deployment
- **Log4j2** for modern logging with full feature set
- **Flink Web UI** enabled for job monitoring
- **RocksDB State Backend** available for production workloads

**JAR Size:** ~18MB optimized deployment JAR (Flink dependencies excluded)

## Usage

### Running Jobs in VS Code
1. **Open the project** in VS Code at workspace root (`DSS25_project/`)
2. **Go to Run and Debug** view (Ctrl+Shift+D / Cmd+Shift+D)
3. **Select the job** you want to run from the dropdown
4. **Press F5** to start debugging or **Ctrl+F5** to run without debugging
5. **Access Web UI** at `http://localhost:8081` to monitor job execution

### Running Jobs from Command Line
```bash
# Navigate to project directory
cd flink-latency-25

# Set environment variable (relative to workspace root)
export FLINK_CONF_DIR=./flink-latency-25/src/main/resources

# Run a job with Maven
mvn exec:java -Dexec.mainClass="com.ververica.lablatency.job.WindowingJob" \
  -Dexec.args="--kafka localhost:9093 --topic lablatency"
```

### Building the Deployment JAR
```bash
# Build optimized JAR for deployment
mvn clean package

# JAR location: target/flink-latency-25-1.0-SNAPSHOT.jar
# Size: ~18MB (Flink dependencies excluded, Kafka connector included)
```

## Overview

This project demonstrates various Apache Flink optimization techniques for reducing latency in stream processing applications using product data streams. Built with modern Apache Flink 1.20.2 APIs and optimized for performance testing.

### Project Features

- **Modern Flink 1.20.2**: Uses latest APIs and best practices
- **Product Data Model**: Processes product events with pricing and metadata
- **Maven Build System**: Optimized dependencies with 18MB deployment JAR
- **Kafka Integration**: Modern Kafka Source API with connector included
- **Comprehensive Configuration**: Environment-based configuration management
- **Advanced Logging**: Log4j2 with component-specific configurations
- **VS Code Integration**: Complete launch configurations for development
- **Web UI Monitoring**: Built-in Flink dashboard at localhost:8081

## Jobs

### IngestingJob
Generates product data streams and publishes them to Kafka. This job simulates realistic product events with configurable load patterns.

**Key Features:**
- Configurable spike intervals for load testing
- Adjustable processing delays for performance testing
- Modern Kafka sink connector API
- Realistic product data generation

**Run Configuration:**
```
--job-name IngestingJob --kafka localhost:9092 --topic lablatency --parallelism 1
```

### WindowingJob
Applies sliding time windows to aggregate product data by product name. This is the baseline job demonstrating fundamental Flink streaming capabilities.

**Key Features:**
1. Incremental aggregation using `AggregateFunction`
2. Proper watermark handling with bounded out-of-orderness
3. Event time lag metrics for performance monitoring
4. Product price aggregation over time windows

**Run Configuration:**
```
--job-name WindowingJob --kafka localhost:9093 --topic lablatency 
--group lablatency-windowing --slide-size 10 --out-of-orderness 250
```

### WindowingJobNoAggregation
Alternative windowing implementation without incremental aggregation - demonstrates performance differences when using different aggregation strategies.

### EnrichingJobSync / EnrichingJobAsync
Enriches product data with external service information using synchronous or asynchronous processing patterns.

**Features:**
- Simulates external service calls with configurable response times
- Demonstrates latency impact of sync vs async processing
- Modern Java Random API implementation

**Run Configuration:**
```
--response-time-min 50 --response-time-max 150 --out-of-orderness 250
```

### EnrichingJobAsync
Asynchronous version of the enriching job that uses async I/O for external service calls.

**Key Features:**
- Async I/O for external service calls
- Configurable capacity and timeout settings
- Demonstrates significant latency improvements over synchronous processing

### SortingJobPerEventTimer
Implements product event sorting using per-event timers. Useful for demonstrating timer performance under high load scenarios.

### SortingJobCoalescedTimer
Optimized sorting implementation that coalesces timers to reduce system overhead.

**Key Features:**
- Timer coalescing by rounding timer times to configurable intervals
- Significantly reduces timer overhead under high load
- Demonstrates advanced Flink timer optimization techniques

## Build and Run

### Prerequisites
- Java 11 or higher
- Apache Maven 3.6+
- Apache Kafka (for running the jobs)

### Build
```bash
mvn clean package
```

### Running Jobs

Each job can be run with various parameters:

```bash
# Run the ingesting job
java -cp target/flink-latency-25-1.0-SNAPSHOT.jar com.ververica.lablatency.job.IngestingJob \
  --kafka localhost:9092 \
  --topic lablatency \
  --spike-interval 1 \
  --wait-micro 0

# Run the windowing job
java -cp target/flink-latency-25-1.0-SNAPSHOT.jar com.ververica.lablatency.job.WindowingJob \
  --kafka localhost:9092 \
  --topic lablatency \
  --group lablatency \
  --slide-size 10 \
  --out-of-orderness 250
```

## Optimization Techniques

The jobs demonstrate the following optimization techniques:

#### Direct Latency Optimizations:
- Optimization 1: Increase Parallelism
- Optimization 2: Use HashMap/Filesystem State Backend  
- Optimization 3: Reduce Watermark Interval
- Optimization 4: Reduce Network Buffer Timeout
- Optimization 5: Use Incremental Aggregation
- Optimization 6: Use Asynchronous I/O
- Optimization 7: Tune Checkpointing

#### Throughput Optimizations (that improve latency):
- Optimization 8: Timer Coalescing
- Optimization 9: Optimize User Code
- Optimization 10: Choose Right Serialization
- Optimization 11: Enable Object Reuse



### Optimization 1: Increase Parallelism
Increase the job parallelism, e.g., from 2 to 3. Best to have the number of the partitions of your Kafka topic divisible by 2 and by 3 to avoid data skew.

### Optimization 2: Use HashMap/Filesystem State Backend
Use the hashmap/filesystem state backend by changing the configuration from:

```
state.backend: rocksdb
taskmanager.memory.managed.fraction: '0.4'
```

to:

```
state.backend: hashmap
taskmanager.memory.managed.fraction: '0.0'
```

### Optimization 3: Reduce Watermark Interval
Reduce the watermark interval from the default `200 ms` to `100 ms`:

```
pipeline.auto-watermark-interval: 100 ms
```

### Optimization 4: Reduce Network Buffer Timeout
Reduce the network buffer timeout from the default `100 ms` to `10 ms`:

```
execution.buffer-timeout: 10 ms
```

### Optimization 5: Use Incremental Aggregation
Use incremental aggregation with `AggregateFunction` instead of collecting all events in `ProcessWindowFunction`.

### Optimization 6: Use Asynchronous I/O
Replace synchronous external service calls with asynchronous I/O operations.

### Optimization 7: Timer Coalescing
Coalesce timers by rounding timer registration times to reduce timer overhead.

## Performance Testing

To measure the impact of each optimization:

1. Start with the baseline job (WindowingJob)
2. Apply optimizations incrementally
3. Measure latency using the built-in metrics
4. Compare throughput and latency characteristics

The jobs include built-in metrics for measuring event time lag and other performance indicators.

## Configuration Parameters

Common parameters across jobs:

- `--kafka`: Kafka bootstrap servers (default: localhost:9093)
- `--topic`: Kafka topic name (default: lablatency)
- `--group`: Consumer group ID (default: lablatency)
- `--job-name`: Flink job name
- `--out-of-orderness`: Out-of-orderness tolerance in milliseconds (default: 250)
- `--slide-size`: Window slide size in seconds (default: 10)

Job-specific parameters:

- `--spike-interval`: Ingestion spike interval in minutes (IngestingJob)
- `--wait-micro`: Processing delay in microseconds (IngestingJob)
- `--response-time-min/max`: External service response time range (EnrichingJob*)
- `--round-timer-to`: Timer coalescing interval (SortingJobCoalescedTimer)

## Data Model

### Product Class
```java
public class Product {
    private int productId;
    private double price;
    private String product_name;
    private String productInformation;
    // ... getters and setters
}
```

### WindowedProduct Class
```java
public class WindowedProduct {
    private long windowStart;
    private long windowEnd;
    private String product_name;
    private long eventsPerWindow;
    private double sumPerWindow;
    // ... getters and setters
}
```

## Technical Implementation

- **Apache Flink 1.20.2**: Latest stable release with modern APIs
- **Kafka Connector 3.4.0-1.20**: Modern Kafka Source and Sink APIs
- **Maven Build System**: Optimized dependency management and JAR creation
- **Java 11+**: Modern Java features and best practices
- **Log4j2**: Advanced logging with component-specific configurations
- **Environment Configuration**: Flexible setup for development and deployment
