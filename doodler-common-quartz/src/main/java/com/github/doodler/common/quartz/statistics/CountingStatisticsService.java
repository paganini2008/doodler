package com.github.doodler.common.quartz.statistics;

import java.util.Arrays;
import java.util.List;
import com.github.doodler.common.timeseries.LoggingOverflowDataHandler;
import com.github.doodler.common.timeseries.OverflowDataHandler;
import com.github.doodler.common.timeseries.Sampler;
import com.github.doodler.common.timeseries.SamplerImpl;
import com.github.doodler.common.timeseries.StringSamplerService;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * @Description: CountingStatisticsService
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
public class CountingStatisticsService extends StringSamplerService<RuntimeCounter> {

    public CountingStatisticsService() {
        this(Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    public CountingStatisticsService(List<OverflowDataHandler<String, String, RuntimeCounter>> dataHandlers) {
        super(5, TimeWindowUnit.MINUTES, 60, dataHandlers);
    }

    @Override
    protected Sampler<RuntimeCounter> getEmptySampler(String category, String dimension, long timestampMillis) {
        return new SamplerImpl<RuntimeCounter>(timestampMillis, new RuntimeCounter());
    }
}