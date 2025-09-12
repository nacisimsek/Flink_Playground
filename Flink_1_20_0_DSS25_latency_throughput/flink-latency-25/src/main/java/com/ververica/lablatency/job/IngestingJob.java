/*
 * Copyright 2021 Ververica GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ververica.lablatency.job;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.lablatency.event.Product;
import com.ververica.lablatency.source.ProductSource;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/** This is a Flink job. */
public class IngestingJob {

    static class KafkaSerSchema implements KafkaRecordSerializationSchema<Product> {

        private static final Logger LOG = LoggerFactory.getLogger(KafkaSerSchema.class);
        private ObjectMapper mapper;
        private final String topic;

        public KafkaSerSchema(String topic) {
            this.topic = topic;
            this.mapper = new ObjectMapper();
        }

        public void open(SerializationSchema.InitializationContext context) throws Exception {
            this.mapper = new ObjectMapper();
        }

        @Override
        public ProducerRecord<byte[], byte[]> serialize(
                Product product, KafkaRecordSerializationSchema.KafkaSinkContext kafkaSinkContext, @Nullable Long aLong) {
            try {
                return new ProducerRecord<>(
                        this.topic,
                        null,
                        System.currentTimeMillis(),
                        null,
                        this.mapper.writeValueAsBytes(product));
            } catch (JsonProcessingException e) {
                LOG.error("Failed to serialize product: " + e.getMessage());
                return null;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        // Debug logging to see what arguments are received
        System.out.println("=== DEBUGGING ARGS ===");
        System.out.println("Number of arguments: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("Arg[" + i + "]: " + args[i]);
        }
        System.out.println("======================");

        final ParameterTool params = ParameterTool.fromArgs(args);

        final String jobName = params.get("job-name", IngestingJob.class.getSimpleName());
        final String kafkaAddress = params.get("kafka", "localhost:9093");
        final String topic = params.get("topic", "lablatency");

        // Debug logging to see parsed parameters
        System.out.println("=== PARSED PARAMS ===");
        System.out.println("Job Name: " + jobName);
        System.out.println("Kafka Address: " + kafkaAddress);
        System.out.println("Topic: " + topic);
        System.out.println("====================");

        // when spikeInterval==1, every minute is a spike, it actually means there is no spikes
        int spikeInterval = params.getInt("spike-interval", 1);
        int waitMicro = params.getInt("wait-micro", 0);

        // Flink environment setup
        Configuration config = GlobalConfiguration.loadConfiguration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        // Make parameters available to Task Managers
        env.getConfig().setGlobalJobParameters(params);

        env.disableOperatorChaining(); // to check throughput

        // Create Kafka sink using new connector
        KafkaSink<Product> producer = KafkaSink.<Product>builder()
                .setBootstrapServers(kafkaAddress)
                .setRecordSerializer(new KafkaSerSchema(topic))
                // Override Kafka properties to handle advertised listener issues in K8s
                .setProperty("client.dns.lookup", "use_all_dns_ips")
                .setProperty("connections.max.idle.ms", "300000")
                .setProperty("request.timeout.ms", "60000")
                .setProperty("reconnect.backoff.ms", "1000")
                .setProperty("reconnect.backoff.max.ms", "10000")
                .build();

        // Use modern Source API with fromSource()
        DataStream<Product> sourceStream = env.fromSource(
                ProductSource.createSource(),
                WatermarkStrategy.noWatermarks(),
                "ProductSource")
                .map(new ProductSource.ProductMapper(spikeInterval, waitMicro))
                .name("ProductMapper")
                .uid("ProductMapper");
                
        sourceStream.sinkTo(producer);

        env.execute(jobName);
    }
}
