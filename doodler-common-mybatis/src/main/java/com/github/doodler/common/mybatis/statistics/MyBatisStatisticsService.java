package com.github.doodler.common.mybatis.statistics;

import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.common.utils.statistics.SampleCollector;
import com.github.doodler.common.utils.statistics.Sampler;
import com.github.doodler.common.utils.statistics.StatisticsService;
import com.github.doodler.common.utils.statistics.TimeWindowSampleCollector;
import com.github.doodler.common.utils.statistics.UserSampler;

/**
 * @Description: MyBatisStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class MyBatisStatisticsService extends StatisticsService<SqlSample> {

    @Override
    protected SampleCollector<SqlSample> getSampleCollector(long timestampMillis) {
        return new TimeWindowSampleCollector<>(5, TimeWindowUnit.MINUTES,
                () -> getEmptySampler(timestampMillis));
    }

    @Override
    protected Sampler<SqlSample> getEmptySampler(long timestampMillis) {
        return new UserSampler<SqlSample>(timestampMillis, new SqlSample());
    }
}