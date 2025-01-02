package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: TimeWindowSamplerService
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public abstract class SimpleSamplerService<C, D, E extends Metric>
        extends SamplerService<C, D, E, Sampler<E>> {

    protected SimpleSamplerService(int span, TimeWindowUnit timeWindowUnit, int maxSize,
            List<OverflowDataHandler<C, D, E>> dataHandlers) {
        super();
        this.span = span;
        this.timeWindowUnit = timeWindowUnit;
        this.maxSize = maxSize;
        this.dataHandlers = dataHandlers;
    }

    protected final int span;
    protected final TimeWindowUnit timeWindowUnit;
    protected final int maxSize;
    protected final List<OverflowDataHandler<C, D, E>> dataHandlers;

    protected TimeZone timeZone = TimeZone.getDefault();

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    protected SampleCollector<E, Sampler<E>> getSampleCollector(C category, D dimension,
            long timestamp) {
        return new TimeWindowSampleCollector<>(span, timeWindowUnit, timeZone, maxSize,
                () -> getEmptySampler(category, dimension, timestamp),
                new OverflowDataStore<C, D, E, Sampler<E>>(category, dimension, dataHandlers));
    }

    public Map<String, Object> sequence(C category, D dimension,
            DateTimeFormatter dateTimeFormatter) {
        DateTimeFormatter dtf = dateTimeFormatter != null ? dateTimeFormatter
                : TimeSeriesConstants.DEFAULT_DATE_TIME_FORMATTER;
        Map<Instant, Object> data = super.sequence(category, dimension);
        Map<String, Object> results = data.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey().atZone(timeZone.toZoneId()).format(dtf), e.getValue()),
                LinkedHashMap::putAll);
        Map<String, Object> emptyMap =
                timeWindowUnit.initializeMap(new Date(), span, maxSize, timeZone, dtf,
                        time -> getEmptySampler(category, dimension, time).getSample().represent());
        emptyMap.putAll(results);
        return emptyMap;
    }

    public void update(C category, D dimension, long timestamp, Consumer<Sampler<E>> consumer) {
        super.collect(category, dimension, timestamp, consumer);
    }

}
