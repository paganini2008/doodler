package io.doodler.common.utils.statistics;

import java.util.Map;

/**
 * @Description: SampleCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public interface SampleCollector<T> {

    Sampler<T> sampler(long ms);
    
    Map<String, Sampler<T>> samplers();
}