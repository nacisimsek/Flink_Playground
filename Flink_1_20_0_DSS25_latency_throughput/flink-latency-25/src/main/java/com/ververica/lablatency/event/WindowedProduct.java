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

import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class WindowedProduct {

    private long windowStart;
    private long windowEnd;
    private String product_name;
    private long eventsPerWindow;
    private double sumPerWindow;

    public WindowedProduct() {}

    public WindowedProduct(
            final long windowStart,
            final long windowEnd,
            final String product_name,
            final long eventsPerWindow,
            final double sumPerWindow) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.product_name = product_name;
        this.eventsPerWindow = eventsPerWindow;
        this.sumPerWindow = sumPerWindow;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(final long windowStart) {
        this.windowStart = windowStart;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(final long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public void setWindow(TimeWindow window) {
        setWindowStart(window.getStart());
        setWindowEnd(window.getEnd());
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(final String product_name) {
        this.product_name = product_name;
    }

    public long getEventsPerWindow() {
        return eventsPerWindow;
    }

    public void setEventsPerWindow(final long eventsPerWindow) {
        this.eventsPerWindow = eventsPerWindow;
    }

    public double getSumPerWindow() {
        return sumPerWindow;
    }

    public void setSumPerWindow(final double sumPerWindow) {
        this.sumPerWindow = sumPerWindow;
    }

    public void addProduct(Product product) {
        addProduct(product.getPrice());
    }

    public void addProduct(double price) {
        sumPerWindow += price;
        ++eventsPerWindow;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WindowedProduct that = (WindowedProduct) o;
        return windowStart == that.windowStart
                && windowEnd == that.windowEnd
                && eventsPerWindow == that.eventsPerWindow
                && Double.compare(that.sumPerWindow, sumPerWindow) == 0
                && Objects.equals(product_name, that.product_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowStart, windowEnd, product_name, eventsPerWindow, sumPerWindow);
    }

    @Override
    public String toString() {
        return "WindowedProduct{"
                + "windowStart="
                + windowStart
                + ", windowEnd="
                + windowEnd
                + ", product_name='"
                + product_name
                + '\''
                + ", eventsPerWindow="
                + eventsPerWindow
                + ", sumPerWindow="
                + sumPerWindow
                + '}';
    }
}
