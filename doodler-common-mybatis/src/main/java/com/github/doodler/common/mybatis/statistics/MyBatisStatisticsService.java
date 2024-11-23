package com.github.doodler.common.mybatis.statistics;

import java.util.Arrays;
import java.util.List;

import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.timeseries.LoggingOverflowDataHandler;
import com.github.doodler.timeseries.OverflowDataHandler;
import com.github.doodler.timeseries.Sampler;
import com.github.doodler.timeseries.SamplerImpl;
import com.github.doodler.timeseries.StringSamplerService;

/**
 * @Description: MyBatisStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class MyBatisStatisticsService extends StringSamplerService<SqlSample> {

    public MyBatisStatisticsService() {
        this(Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    public MyBatisStatisticsService(List<OverflowDataHandler<String, String, SqlSample>> dataHandlers) {
        this(5, TimeWindowUnit.MINUTES, 60, dataHandlers);
    }

    public MyBatisStatisticsService(int span,
                                    TimeWindowUnit timeWindowUnit,
                                    int maxSize,
                                    List<OverflowDataHandler<String, String, SqlSample>> dataHandlers) {
        super(span, timeWindowUnit, maxSize, dataHandlers);
    }

    @Override
    protected Sampler<SqlSample> getEmptySampler(String category, String dimension, long timestamp) {
        return new SamplerImpl<SqlSample>(timestamp, new SqlSample());
    }
}