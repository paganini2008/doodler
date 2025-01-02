package com.github.doodler.common.timeseries;

import static com.github.doodler.common.timeseries.TimeSeriesConstants.DEFAULT_DATE_TIME_FORMATTER;
import static com.github.doodler.common.timeseries.TimeSeriesConstants.DEFAULT_TIMEZONE;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.github.doodler.common.utils.TimeWindowMap;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: TimeWindowSampleCollector
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public class TimeWindowSampleCollector<C, D, E extends Metric, T extends Sampler<E>>
        implements SampleCollector<E, T> {

    private final TimeWindowMap<T> samplers;
    private final Supplier<T> supplier;

    public TimeWindowSampleCollector(int span, TimeWindowUnit timeWindowUnit, TimeZone timeZone,
            int maxSize, Supplier<T> supplier, OverflowDataStore<C, D, E, T> overflowDataStore) {
        this.samplers =
                new TimeWindowMap<>(span, timeWindowUnit, timeZone, maxSize, overflowDataStore);
        this.supplier = supplier;
    }

    @Override
    public T sampler(long ms) {
        Instant ins = Instant.ofEpochMilli(ms);
        T sampler = samplers.get(ins);
        if (sampler == null) {
            samplers.putIfAbsent(ins, supplier.get());
            sampler = samplers.get(ins);
        }
        return sampler;
    }

    @Override
    public Map<Instant, T> samplers() {
        return samplers.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
                .toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    @Override
    public Map<String, T> samplers(TimeZone timeZone, DateTimeFormatter dtf) {
        return samplers.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
                LinkedHashMap::new,
                (m, e) -> m.put(
                        e.getKey()
                                .atZone(timeZone != null ? timeZone.toZoneId()
                                        : DEFAULT_TIMEZONE.toZoneId())
                                .toLocalDateTime()
                                .format(dtf != null ? dtf : DEFAULT_DATE_TIME_FORMATTER),
                        e.getValue()),
                LinkedHashMap::putAll);
    }

}
