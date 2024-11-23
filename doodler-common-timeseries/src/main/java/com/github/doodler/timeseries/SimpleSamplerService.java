package com.github.doodler.timeseries;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: TimeWindowSamplerService
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public abstract class SimpleSamplerService<C, D, E extends Metric> extends SamplerService<C, D, E, Sampler<E>> {

    protected SimpleSamplerService(int span,
                                   TimeWindowUnit timeWindowUnit,
                                   int maxSize,
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

    @Override
    protected SampleCollector<E, Sampler<E>> getSampleCollector(C category, D dimension, long timestamp) {
        return new TimeWindowSampleCollector<>(span, timeWindowUnit, timeZone, maxSize, () -> getEmptySampler(category,
                dimension,
                timestamp), new OverflowDataStore<C, D, E, Sampler<E>>(category,
                        dimension, dataHandlers));
    }

    @Override
    protected Map<String, Object> render(C category, D dimension, Map<String, Object> data) {
        if (data.size() < maxSize) {
            Map<String, Object> emptyMap = timeWindowUnit.initializeMap(new Date(), span, maxSize, timeZone,
                    dateTimeFormatter,
                    time -> getEmptySampler(
                            category, dimension, time).getSample().represent());
            emptyMap.putAll(data);
            return emptyMap;
        }
        return data;
    }

    public void update(C category, D dimension, long timestamp, Consumer<Sampler<E>> consumer) {
        super.collect(category, dimension, timestamp, consumer);
    }

}
