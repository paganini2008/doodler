package io.doodler.cache.redis;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.doodler.cache.statistics.CacheSample;
import io.doodler.cache.statistics.CacheStatisticsService;
import io.doodler.common.ApiResult;
import io.doodler.common.utils.statistics.Sampler;

/**
 * @Description: RedisCacheStatisticsEndpoint
 * @Author: Fred Feng
 * @Date: 14/04/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "redis", matchIfMissing = true)
@RestControllerEndpoint(id = "redisCacheStatistics")
@Component
public class RedisCacheStatisticsEndpoint {

	@Autowired
	private CacheStatisticsService statisticsService;

	@GetMapping("/sampler")
    public ApiResult<CacheSample> sampler(@RequestParam("name") String name, @RequestParam("identifier") String identifier) {
        Sampler<CacheSample> sampler = statisticsService.sampler(name, identifier, System.currentTimeMillis());
        return ApiResult.ok(sampler.getSample());
    }
	
	@GetMapping("/summarize")
    public ApiResult<CacheSample> summarize(@RequestParam("name") String name, @RequestParam("identifier") String identifier) {
        Sampler<CacheSample> sampler = statisticsService.summarize(name, identifier);
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, CacheSample>> sequence(@RequestParam("name") String name,
                                                       @RequestParam("identifier") String identifier) {
        Map<String, Sampler<CacheSample>> samplers = statisticsService.sequence(name, identifier);
        return ApiResult.ok(
                samplers.entrySet().stream().collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().getSample()),
                        LinkedHashMap::putAll));
    }
    
    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, CacheSample>> summarizeAll(@RequestParam("name") String name) {
        Map<String, Sampler<CacheSample>> samplers = statisticsService.summarize(name);
        return ApiResult.ok(
                samplers.entrySet().stream().collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().getSample()),
                        LinkedHashMap::putAll));
    }
}