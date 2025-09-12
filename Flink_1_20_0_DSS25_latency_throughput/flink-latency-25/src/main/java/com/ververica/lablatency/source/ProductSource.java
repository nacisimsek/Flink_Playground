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
package com.ververica.lablatency.source;

import org.apache.flink.api.connector.source.lib.NumberSequenceSource;

import com.ververica.lablatency.event.Product;
import com.ververica.lablatency.util.TimeUtil;

import java.util.List;
import java.util.Random;

/**
 * ProductSource using modern Flink 1.20+ Source API.
 * Generates test products with configurable spike intervals.
 * Uses NumberSequenceSource as base and maps numbers to products.
 */
public class ProductSource {
    
    /**
     * Creates the modern Source for products.
     * Returns a NumberSequenceSource that can be mapped to products.
     */
    public static NumberSequenceSource createSource() {
        // Create an infinite number sequence that will be mapped to products
        return new NumberSequenceSource(0, Long.MAX_VALUE);
    }
    
    /**
     * Maps sequence numbers to products with spike behavior.
     * Use this with .map() on the DataStream from the source.
     */
    public static class ProductMapper implements org.apache.flink.api.common.functions.MapFunction<Long, Product> {
        private final Random rand = new Random();
        private final int spikeInterval;
        private final int waitMicro;
        private final List<Product> products;
        
        public ProductMapper(int spikeInterval, int waitMicro) {
            this.spikeInterval = spikeInterval;
            this.waitMicro = waitMicro;
            this.products = ProductGenerator.generateProducts();
        }
        
        @Override
        public Product map(Long sequenceNumber) throws Exception {
            // Simulate product spikes every spikeInterval minutes
            if (System.currentTimeMillis() / 1000 / 60 % spikeInterval != 0) {
                Thread.sleep(1);
            }
            
            TimeUtil.busyWaitMicros(waitMicro);
            
            int index = rand.nextInt(products.size());
            return products.get(index);
        }
    }
}
