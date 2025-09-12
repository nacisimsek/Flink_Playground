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
package com.ververica.lablatency.event;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Product implements Serializable {

    private int productId;
    private double price;
    private String product_name;
    private String productInformation;

    public Product() {}

    public Product(
            final int productId,
            final double price,
            final String product_name,
            final String productInformation) {
        this.productId = productId;
        this.price = price;
        this.product_name = product_name;
        this.productInformation = productInformation;
    }

    public Product(Product product) {
        this.productId = product.getProductId();
        this.price = product.getPrice();
        this.product_name = product.getProduct_name();
        this.productInformation = product.getProductInformation();
    }

    public String getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(final String productInformation) {
        this.productInformation = productInformation;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(final int productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(final String product_name) {
        this.product_name = product_name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Product that = (Product) o;
        return productId == that.productId
                && Double.compare(that.price, price) == 0
                && Objects.equals(product_name, that.product_name)
                && Objects.equals(productInformation, that.productInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, price, product_name, productInformation);
    }

    @Override
    public String toString() {
        return "Product{"
                + "productId="
                + productId
                + ", price="
                + price
                + ", product_name='"
                + product_name
                + '\''
                + ", productInformation='"
                + productInformation
                + '\''
                + '}';
    }
}
