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
import com.github.doodler.common.utils.statistics.Sampler;
import com.github.doodler.common.utils.statistics.StatisticsService;

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
            @RequestParam("name") String name,
            @RequestParam("identifier") String identifier) {
        Sampler<CacheSample> sampler = chooseStatisticsService(local).sampler(name, identifier, System.currentTimeMillis());
        return ApiResult.ok(sampler.getSample());
    }
    
    @GetMapping("/summarize")
    public ApiResult<CacheSample> summarize(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("name") String name,
            @RequestParam("identifier") String identifier) {
        Sampler<CacheSample> sampler = chooseStatisticsService(local).summarize(name, identifier);
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, CacheSample>> sequence(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("name") String name,
            @RequestParam("identifier") String identifier) {
        Map<String, Sampler<CacheSample>> samplers = chooseStatisticsService(local).sequence(name, identifier);
        return ApiResult.ok(
                samplers.entrySet().stream().collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().getSample()),
                        LinkedHashMap::putAll));
    }
    
    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, CacheSample>> summarizeAll(
            @RequestParam(name = "local", required = false, defaultValue = "true") boolean local,
            @RequestParam("name") String name) {
        Map<String, Sampler<CacheSample>> samplers = chooseStatisticsService(local).summarize(name);
        return ApiResult.ok(
                samplers.entrySet().stream().collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().getSample()),
                        LinkedHashMap::putAll));
    }

    private StatisticsService<CacheSample> chooseStatisticsService(boolean local) {
        return local ? localCacheStatisticsService : remoteCacheStatisticsService;
    }
}