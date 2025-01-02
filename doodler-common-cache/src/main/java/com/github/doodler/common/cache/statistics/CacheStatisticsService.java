package com.github.doodler.common.cache.statistics;

import java.util.Arrays;
import java.util.List;
import com.github.doodler.common.timeseries.LoggingOverflowDataHandler;
import com.github.doodler.common.timeseries.OverflowDataHandler;
import com.github.doodler.common.timeseries.Sampler;
import com.github.doodler.common.timeseries.SamplerImpl;
import com.github.doodler.common.timeseries.StringSamplerService;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * @Description: CacheStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class CacheStatisticsService extends StringSamplerService<CacheSample> {

    public CacheStatisticsService() {
        this(Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    public CacheStatisticsService(
                                  List<OverflowDataHandler<String, String, CacheSample>> dataHandlers) {
        super(5, TimeWindowUnit.MINUTES, 60, dataHandlers);
    }

    @Override
    protected Sampler<CacheSample> getEmptySampler(String category, String dimension, long timestampMillis) {
        return new SamplerImpl<CacheSample>(timestampMillis, new CacheSample());
    }
}