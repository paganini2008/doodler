package com.github.doodler.common.timeseries;

import java.util.Arrays;
import java.util.List;

import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: StringSimpleUserSamplerService
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public abstract class StringSimpleUserSamplerService<E extends UserMetric<E>> extends
        SimpleUserSamplerService<String, String, E> {

    protected StringSimpleUserSamplerService(int span, TimeWindowUnit timeWindowUnit, int maxSize) {
        this(span, timeWindowUnit, maxSize, Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    protected StringSimpleUserSamplerService(int span, TimeWindowUnit timeWindowUnit, int maxSize,
                                             List<OverflowDataHandler<String, String, E>> dataHandlers) {
        super(span, timeWindowUnit, maxSize, dataHandlers);
    }

}
