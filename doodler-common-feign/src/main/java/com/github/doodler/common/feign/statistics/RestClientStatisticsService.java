package com.github.doodler.common.feign.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.timeseries.LoggingOverflowDataHandler;
import com.github.doodler.timeseries.OverflowDataHandler;
import com.github.doodler.timeseries.Sampler;
import com.github.doodler.timeseries.SamplerImpl;
import com.github.doodler.timeseries.StringSamplerService;

/**
 * @Description: RestClientStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class RestClientStatisticsService extends StringSamplerService<HttpSample> implements ManagedBeanLifeCycle {

    public RestClientStatisticsService() {
        this(Arrays.asList(new LoggingOverflowDataHandler<>()));
    }

    public RestClientStatisticsService(
                                       List<OverflowDataHandler<String, String, HttpSample>> dataHandlers) {
        super(5, TimeWindowUnit.MINUTES, 60, dataHandlers);
    }

    private TpsCalculator tpsCalculator;

    @Override
    protected Sampler<HttpSample> getEmptySampler(String category, String dimension, long timestampMillis) {
        return new SamplerImpl<HttpSample>(timestampMillis, new HttpSample());
    }

    public void prepare(String category, String dimension, long timestampMillis, Consumer<Sampler<HttpSample>> consumer) {
        this.update(category, dimension, timestampMillis, consumer);
    }

    @Override
    public void update(String category, String dimension, long timestampMillis, Consumer<Sampler<HttpSample>> consumer) {
        super.update(category, dimension, timestampMillis, consumer);
        tpsCalculator.incr(category, dimension);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        tpsCalculator = new TpsCalculator(1, TimeUnit.SECONDS, this);
        tpsCalculator.start();
    }

    @Override
    public void destroy() throws Exception {
        if (tpsCalculator != null) {
            tpsCalculator.stop();
        }
    }
}