from pyflink.table import EnvironmentSettings, StreamTableEnvironment, DataTypes, Schema, FormatDescriptor, TableDescriptor, TableEnvironment
from pyflink.datastream import StreamExecutionEnvironment

env = StreamExecutionEnvironment.get_execution_environment()
env.enable_checkpointing(10000)  # checkpoint every 10000 milliseconds (10 seconds)

t_env = StreamTableEnvironment.create(env)



# Define the simplified schema for the Kafka JSON structure
kafka_source_schema = Schema.new_builder() \
    .column("payload", DataTypes.ROW([
        DataTypes.FIELD("before", DataTypes.ROW([
            DataTypes.FIELD("customerId", DataTypes.BIGINT()),
            DataTypes.FIELD("customerFName", DataTypes.STRING()),
            DataTypes.FIELD("customerLName", DataTypes.STRING()),
            DataTypes.FIELD("customerEmail", DataTypes.STRING()),
            DataTypes.FIELD("customerPassword", DataTypes.STRING()),
            DataTypes.FIELD("customerStreet", DataTypes.STRING()),
            DataTypes.FIELD("customerCity", DataTypes.STRING()),
            DataTypes.FIELD("customerState", DataTypes.STRING()),
            DataTypes.FIELD("customerZipcode", DataTypes.BIGINT()),
        ]).nullable()),
        DataTypes.FIELD("after", DataTypes.ROW([
            DataTypes.FIELD("customerId", DataTypes.BIGINT()),
            DataTypes.FIELD("customerFName", DataTypes.STRING()),
            DataTypes.FIELD("customerLName", DataTypes.STRING()),
            DataTypes.FIELD("customerEmail", DataTypes.STRING()),
            DataTypes.FIELD("customerPassword", DataTypes.STRING()),
            DataTypes.FIELD("customerStreet", DataTypes.STRING()),
            DataTypes.FIELD("customerCity", DataTypes.STRING()),
            DataTypes.FIELD("customerState", DataTypes.STRING()),
            DataTypes.FIELD("customerZipcode", DataTypes.BIGINT()),
        ]).nullable()),
        DataTypes.FIELD("op", DataTypes.STRING()),
        DataTypes.FIELD("ts_ms", DataTypes.BIGINT()),
    ])) \
    .build()

# Define Kafka source
kafka_source = TableDescriptor.for_connector("kafka") \
    .option("topic", "dbserver4.public.customers1") \
    .option("properties.bootstrap.servers", "kafka:9092") \
    .option("properties.group.id", "flink_group") \
    .option("scan.startup.mode", "earliest-offset") \
    .format("json") \
    .schema(kafka_source_schema) \
    .build()

t_env.create_temporary_table("kafka_source", kafka_source)

# Define MinIO (S3) sink with the schema matching the SELECT query
minio_sink_schema = Schema.new_builder() \
    .column("before_customerId", DataTypes.BIGINT()) \
    .column("before_customerFName", DataTypes.STRING()) \
    .column("before_customerLName", DataTypes.STRING()) \
    .column("before_customerEmail", DataTypes.STRING()) \
    .column("before_customerPassword", DataTypes.STRING()) \
    .column("before_customerStreet", DataTypes.STRING()) \
    .column("before_customerCity", DataTypes.STRING()) \
    .column("before_customerState", DataTypes.STRING()) \
    .column("before_customerZipcode", DataTypes.BIGINT()) \
    .column("after_customerId", DataTypes.BIGINT()) \
    .column("after_customerFName", DataTypes.STRING()) \
    .column("after_customerLName", DataTypes.STRING()) \
    .column("after_customerEmail", DataTypes.STRING()) \
    .column("after_customerPassword", DataTypes.STRING()) \
    .column("after_customerStreet", DataTypes.STRING()) \
    .column("after_customerCity", DataTypes.STRING()) \
    .column("after_customerState", DataTypes.STRING()) \
    .column("after_customerZipcode", DataTypes.BIGINT()) \
    .column("operation", DataTypes.STRING()) \
    .column("timestamp_ms", DataTypes.BIGINT()) \
    .build()

# Sink to MinIO as CSV
minio_sink = TableDescriptor.for_connector("filesystem") \
  .option("path", "s3a://dataset/naci") \
  .format("json") \
  .option("sink.partition-commit.policy.kind", "success-file") \
  .option("sink.rolling-policy.file-size", "1KB") \
  .option("sink.rolling-policy.rollover-interval", "1s") \
  .schema(minio_sink_schema) \
  .build()

t_env.create_temporary_table("minio_sink", minio_sink)

# Insert data from Kafka source to MinIO sink
minio_result=t_env.execute_sql("""
    INSERT INTO minio_sink
    SELECT
        payload.before.customerId AS before_customerId,
        payload.before.customerFName AS before_customerFName,
        payload.before.customerLName AS before_customerLName,
        payload.before.customerEmail AS before_customerEmail,
        payload.before.customerPassword AS before_customerPassword,
        payload.before.customerStreet AS before_customerStreet,
        payload.before.customerCity AS before_customerCity,
        payload.before.customerState AS before_customerState,
        payload.before.customerZipcode AS before_customerZipcode,
        payload.after.customerId AS after_customerId,
        payload.after.customerFName AS after_customerFName,
        payload.after.customerLName AS after_customerLName,
        payload.after.customerEmail AS after_customerEmail,
        payload.after.customerPassword AS after_customerPassword,
        payload.after.customerStreet AS after_customerStreet,
        payload.after.customerCity AS after_customerCity,
        payload.after.customerState AS after_customerState,
        payload.after.customerZipcode AS after_customerZipcode,
        payload.op AS operation,
        payload.ts_ms AS timestamp_ms
    FROM kafka_source
""")