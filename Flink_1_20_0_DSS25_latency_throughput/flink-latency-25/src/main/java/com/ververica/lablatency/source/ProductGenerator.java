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

import com.ververica.lablatency.event.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductGenerator {
    public static final Logger LOG = LoggerFactory.getLogger(ProductGenerator.class);

    public static final int NUM_OF_PRODUCTS = 100_000;
    public static final int NUM_OF_PRODUCT_ID = 100;
    public static final int LEN_OF_INFO = 64;
    public static final int RANDOM_SEED = 1;

    public static List<Product> generateProducts() {
        Random rand = new Random(RANDOM_SEED);
        final List<String> product_names = readProductNamesFromFile();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < NUM_OF_PRODUCTS; i++) {
            Product aProduct =
                    new Product(
                            rand.nextInt(NUM_OF_PRODUCT_ID),
                            rand.nextDouble() * 1000, // Price range 0-1000
                            product_names.get(rand.nextInt(product_names.size())),
                            "More info: " + RandomStringUtils.randomAlphabetic(LEN_OF_INFO));
            products.add(aProduct);
        }
        return products;
    }

    private static List<String> readProductNamesFromFile() {
        List<String> product_names = new ArrayList<>();
        try (InputStream is = ProductGenerator.class.getResourceAsStream("/product_20K.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String productName;
            while ((productName = br.readLine()) != null) {
                product_names.add(productName);
            }
        } catch (IOException e) {
            LOG.error("Unable to read product names from file.", e);
            throw new RuntimeException(e);
        }
        return product_names;
    }
}
