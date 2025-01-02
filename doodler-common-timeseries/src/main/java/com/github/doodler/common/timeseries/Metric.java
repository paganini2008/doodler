package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: Metric
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public interface Metric {

    default long getTimestamp() {
        return System.currentTimeMillis();
    }

    default Object represent() {
        return this;
    }
}
