package com.github.doodler.common.cache.redis;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cache.statistics.CacheSample;
import com.github.doodler.common.cache.statistics.CacheStatisticsService;
import com.github.doodler.common.timeseries.Sampler;

/**
 * @Description: RedisCacheStatisticsEndpoint
 * @Author: Fred Feng
 * @Date: 14/04/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "redis",
        matchIfMissing = true)
@RestControllerEndpoint(id = "redisCacheStatistics")
@Component
public class RedisCacheStatisticsEndpoint {

    @Autowired
    private CacheStatisticsService statisticsService;

    @GetMapping("/sampler")
    public ApiResult<CacheSample> sampler(@RequestParam("catelog") String catelog,
            @RequestParam("dimension") String dimension) {
        Sampler<CacheSample> sampler =
                statisticsService.sampler(catelog, dimension, System.currentTimeMillis());
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/summarize")
    public ApiResult<CacheSample> summarize(@RequestParam("catelog") String catelog,
            @RequestParam("dimension") String dimension) {
        Sampler<CacheSample> sampler = statisticsService.summarize(catelog, dimension);
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, Object>> sequence(@RequestParam("catelog") String catelog,
            @RequestParam("dimension") String dimension) {
        Map<String, Object> data = statisticsService.sequence(catelog, dimension, null);
        return ApiResult.ok(data);
    }

    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, CacheSample>> summarizeAll(
            @RequestParam("catelog") String catelog) {
        Map<String, Sampler<CacheSample>> samplers = statisticsService.summarize(catelog);
        return ApiResult.ok(samplers.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue().getSample()), LinkedHashMap::putAll));
    }
}
