package com.github.doodler.common.feign.actuator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.github.doodler.common.feign.statistics.HttpSample;
import com.github.doodler.common.feign.statistics.RestClientStatisticsService;
import com.github.doodler.common.utils.LangUtils;
import com.github.doodler.common.utils.MapUtils;

import lombok.RequiredArgsConstructor;

/**
 * @Description: RestClientStatisticsHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RestClientStatisticsHealthIndicator extends AbstractHealthIndicator {

    private final RestClientStatisticsService statisticsService;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        builder.up();
        Map<String, Object> map = null;
        Map<String, Object> samplers = statisticsService.rank("action",
                (a, b) -> LangUtils.compareTo(b.getSample().getTotalExecutionCount(),
                        a.getSample().getTotalExecutionCount()), 10);
        if (MapUtils.isNotEmpty(samplers)) {
            map = samplers.entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), ((HttpSample) e.getValue()).getTotalExecutionCount()),
                    LinkedHashMap::putAll);
            builder.withDetail("totalExecutionCount", map);
        }

        samplers = statisticsService.rank("action",
                (a, b) -> LangUtils.compareTo(b.getSample().getFailurePercent(), a.getSample().getFailurePercent()), 10);
        if (MapUtils.isNotEmpty(samplers)) {
            map = samplers.entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), ((HttpSample) e.getValue()).getFailurePercent()),
                    LinkedHashMap::putAll);
            builder.withDetail("successPercent", map);
        }

        samplers = statisticsService.rank("action",
                (a, b) -> LangUtils.compareTo(b.getSample().getAverageExecutionTime(),
                        a.getSample().getAverageExecutionTime()), 10);
        if (MapUtils.isNotEmpty(samplers)) {
            map = samplers.entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), ((HttpSample) e.getValue()).getAverageExecutionTime()),
                    LinkedHashMap::putAll);
            builder.withDetail("averageExecutionTime", map);
        }

        samplers = statisticsService.rank("action",
                (a, b) -> LangUtils.compareTo(b.getSample().getTps(), a.getSample().getTps()), 10);
        if (MapUtils.isNotEmpty(samplers)) {
            map = samplers.entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), ((HttpSample) e.getValue()).getTps()),
                    LinkedHashMap::putAll);
            builder.withDetail("tps", map);
        }

        samplers = statisticsService.rank("action",
                (a, b) -> LangUtils.compareTo(b.getSample().getConcurrentCount(), a.getSample().getConcurrentCount()), 10);
        if (MapUtils.isNotEmpty(samplers)) {
            map = samplers.entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), ((HttpSample) e.getValue()).getConcurrentCount()),
                    LinkedHashMap::putAll);
            builder.withDetail("concurrentCount", map);
        }
        builder.build();
    }
}