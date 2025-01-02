package com.github.doodler.common.timeseries;

import java.time.Instant;

/**
 * 
 * @Description: OverflowDataHandler
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public interface OverflowDataHandler<C, D, E extends Metric> {

    void persist(C category, D dimension, Instant instant, E data);

}
