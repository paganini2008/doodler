package com.github.doodler.common.utils.statistics;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.github.doodler.common.utils.TimeWindowMap;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * @Description: TimeWindowSampleCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class TimeWindowSampleCollector<T> implements SampleCollector<T> {

    private final static DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final TimeWindowMap<Sampler<T>> samplers;
    private final Supplier<Sampler<T>> supplier;

    public TimeWindowSampleCollector(int span, TimeWindowUnit timeWindowUnit, Supplier<Sampler<T>> supplier) {
        this.samplers = new TimeWindowMap<>(span, timeWindowUnit);
        this.supplier = supplier;
    }

    @Override
    public Sampler<T> sampler(long ms) {
        Instant ins = Instant.ofEpochMilli(ms);
        Sampler<T> sampler = samplers.get(ins);
        if (sampler == null) {
            samplers.putIfAbsent(ins, supplier.get());
            sampler = samplers.get(ins);
        }
        return sampler;
    }

    @Override
    public Map<String, Sampler<T>> samplers() {
        return samplers.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(LinkedHashMap::new,
                (m, e) -> m.put(
                        e.getKey().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DEFAULT_DATE_TIME_FORMATTER),
                        e.getValue()), LinkedHashMap::putAll);
    }
}