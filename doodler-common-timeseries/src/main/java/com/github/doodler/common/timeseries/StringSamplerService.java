package com.github.doodler.common.timeseries;

import java.util.Arrays;
import java.util.List;

import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: StringSamplerService
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public abstract class StringSamplerService<E extends Metric> extends SimpleSamplerService<String, String, E> {

    protected StringSamplerService(int span,
                                   TimeWindowUnit timeWindowUnit,
                                   int maxSize) {
        this(span, timeWindowUnit, maxSize, Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    protected StringSamplerService(int span,
                                   TimeWindowUnit timeWindowUnit,
                                   int maxSize,
                                   List<OverflowDataHandler<String, String, E>> dataHandlers) {
        super(span, timeWindowUnit, maxSize, dataHandlers);
    }

}