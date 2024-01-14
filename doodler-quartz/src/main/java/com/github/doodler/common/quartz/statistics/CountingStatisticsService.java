package com.github.doodler.common.quartz.statistics;

import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.common.utils.statistics.SampleCollector;
import com.github.doodler.common.utils.statistics.Sampler;
import com.github.doodler.common.utils.statistics.StatisticsService;
import com.github.doodler.common.utils.statistics.TimeWindowSampleCollector;
import com.github.doodler.common.utils.statistics.UserSampler;

/**
 * @Description: CountingStatisticsService
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
public class CountingStatisticsService extends StatisticsService<RuntimeCounter> {

    @Override
    protected SampleCollector<RuntimeCounter> getSampleCollector(long timestampMillis) {
        return new TimeWindowSampleCollector<>(1, TimeWindowUnit.DAYS,
                () -> getEmptySampler(timestampMillis));
    }

    @Override
    protected Sampler<RuntimeCounter> getEmptySampler(long timestampMillis) {
        return new UserSampler<RuntimeCounter>(timestampMillis, new RuntimeCounter());
    }
}