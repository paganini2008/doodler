package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @Description: SampleCollector
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public interface SampleCollector<E, T extends Sampler<E>> {

    T sampler(long ms);

    Map<Instant, T> samplers();

    Map<String, T> samplers(TimeZone timeZone, DateTimeFormatter dtf);
}
