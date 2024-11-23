package com.github.doodler.timeseries;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: LoggingOverflowDataHandler
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@Slf4j
public class LoggingOverflowDataHandler<C, D, E extends Metric> implements OverflowDataHandler<C, D, E> {

    @Override
    public void persist(C category, D dimension, Instant instant, E data) {
        if (log.isTraceEnabled()) {
            log.trace("[{}] - [{}] {}: {}", category, dimension, instant, data.represent());
        }
    }

}
