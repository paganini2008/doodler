package com.github.doodler.common.feign.statistics;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.common.utils.statistics.SampleCollector;
import com.github.doodler.common.utils.statistics.Sampler;
import com.github.doodler.common.utils.statistics.StatisticsService;
import com.github.doodler.common.utils.statistics.TimeWindowSampleCollector;
import com.github.doodler.common.utils.statistics.UserSampler;

/**
 * @Description: RestClientStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class RestClientStatisticsService extends StatisticsService<HttpSample> implements ManagedBeanLifeCycle {

    private final TpsCalculator tpsCalculator = new TpsCalculator(1, TimeUnit.SECONDS, this);

    @Override
    protected SampleCollector<HttpSample> getSampleCollector(long timestampMillis) {
        return new TimeWindowSampleCollector<>(5, TimeWindowUnit.MINUTES,
                () -> getEmptySampler(timestampMillis));
    }

    @Override
    protected Sampler<HttpSample> getEmptySampler(long timestampMillis) {
        return new UserSampler<HttpSample>(timestampMillis, new HttpSample());
    }

    @Override
    public void update(String name, String identifier, long timestampMillis, Consumer<Sampler<HttpSample>> consumer) {
        super.update(name, identifier, timestampMillis, consumer);
        tpsCalculator.incr(name, identifier);
    }

    @Override
    public void destroy() throws Exception {
        tpsCalculator.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        tpsCalculator.start();
    }
}