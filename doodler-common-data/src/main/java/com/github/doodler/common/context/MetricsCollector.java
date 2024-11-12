package com.github.doodler.common.context;

/**
 * @Description: MetricsCollector
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
public interface MetricsCollector {

    void refreshMetrics() throws Exception;
}