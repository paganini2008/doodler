package com.github.doodler.timeseries;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * 
 * @Description: SimpleUserSamplerService
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public abstract class SimpleUserSamplerService<C, D, E extends UserMetric<E>> extends
        UserSamplerService<C, D, E, UserSampler<E>> {

    protected final int span;
    protected final TimeWindowUnit timeWindowUnit;
    protected final int maxSize;
    protected final List<OverflowDataHandler<C, D, E>> dataHandlers;

    protected SimpleUserSamplerService(int span, TimeWindowUnit timeWindowUnit, int maxSize,
                                       List<OverflowDataHandler<C, D, E>> dataHandlers) {
        super();
        this.span = span;
        this.timeWindowUnit = timeWindowUnit;
        this.maxSize = maxSize;
        this.dataHandlers = dataHandlers;
    }

    @Override
    protected SampleCollector<E, UserSampler<E>> getSampleCollector(C category, D dimension, long timestamp) {
        return new TimeWindowSampleCollector<>(span, timeWindowUnit, timeZone, maxSize, () -> getEmptySampler(category,
                dimension,
                timestamp), new OverflowDataStore<C, D, E, UserSampler<E>>(category,
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

}
