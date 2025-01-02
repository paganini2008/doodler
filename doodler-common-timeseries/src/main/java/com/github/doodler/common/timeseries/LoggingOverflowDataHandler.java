package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: LoggingOverflowDataHandler
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@Slf4j
public class LoggingOverflowDataHandler<C, D, E extends Metric>
        implements OverflowDataHandler<C, D, E> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void persist(C category, D dimension, Instant instant, E data) {
        if (log.isTraceEnabled()) {
            log.trace("[{}] - [{}] {}: {}", category, dimension,
                    instant.atZone(ZoneId.systemDefault()).format(dtf), data.represent());
        }
    }

}
