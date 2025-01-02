package com.github.doodler.common.cache.multilevel;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cache.statistics.CacheSample;
import com.github.doodler.common.cache.statistics.CacheStatisticsService;
import com.github.doodler.common.timeseries.Sampler;
import com.github.doodler.common.timeseries.StringSamplerService;

/**
 * @Description: MultiLevelCacheStatisticsEndpoint
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "multilevel")
@RestControllerEndpoint(id = "multiLevelCacheStatistics")
@Component
public class MultiLevelCacheStatisticsEndpoint {

    @Autowired
    @Qualifier("localCacheStatisticsService")
    public CacheStatisticsService localCacheStatisticsService;

    @Autowired
    @Qualifier("remoteCacheStatisticsService")
    private CacheStatisticsService remoteCacheStatisticsService;

    @GetMapping("/sampler")
    public ApiResult<CacheSample> sampler(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("catelog") String catelog, @RequestParam("dimension") String dimension) {
        Sampler<CacheSample> sampler = chooseStatisticsService(local).sampler(catelog, dimension,
                System.currentTimeMillis());
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/summarize")
    public ApiResult<CacheSample> summarize(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("catelog") String catelog, @RequestParam("dimension") String dimension) {
        Sampler<CacheSample> sampler = chooseStatisticsService(local).summarize(catelog, dimension);
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, Object>> sequence(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("catelog") String catelog, @RequestParam("dimension") String dimension) {
        Map<String, Object> data =
                chooseStatisticsService(local).sequence(catelog, dimension, null);
        return ApiResult.ok(data);
    }

    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, CacheSample>> summarizeAll(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("catelog") String catelog) {
        Map<String, Sampler<CacheSample>> samplers =
                chooseStatisticsService(local).summarize(catelog);
        return ApiResult.ok(samplers.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue().getSample()), LinkedHashMap::putAll));
    }

    private StringSamplerService<CacheSample> chooseStatisticsService(boolean local) {
        return local ? localCacheStatisticsService : remoteCacheStatisticsService;
    }
}
