package com.github.doodler.common.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.utils.MatchMode;

/**
 * @Description: CacheManagerEndpoint
 * @Author: Fred Feng
 * @Date: 20/04/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "cacheManager")
@Component
public class CacheManagerEndpoint {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheControl cacheControl;

    @GetMapping("/keys")
    public ApiResult<Map<String, Collection<Object>>> getCacheNameAndKeys() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (CollectionUtils.isEmpty(cacheNames)) {
            return ApiResult.ok(Collections.emptyMap());
        }
        Map<String, Collection<Object>> results = new HashMap<>();
        cacheNames.forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null && cacheManager instanceof CacheKeyManager) {
                Set<Object> cacheKeys = ((CacheKeyManager) cacheManager).getCacheKeys(cacheName);
                if (CollectionUtils.isNotEmpty(cacheKeys)) {
                    results.put(cacheName, cacheKeys);
                }
            }
        });
        return ApiResult.ok(results);
    }

    @PostMapping("/enable")
    public ApiResult<Boolean> enableCache(@RequestParam("enabled") String enabled) {
        if (enabled.equalsIgnoreCase("true")
                || enabled.equalsIgnoreCase("on")
                || enabled.equalsIgnoreCase("yes")
                || enabled.equals("1")) {
            cacheControl.turnOn();
        } else if (enabled.equalsIgnoreCase("false")
                || enabled.equalsIgnoreCase("off")
                || enabled.equalsIgnoreCase("no")
                || enabled.equals("0")) {
            cacheControl.turnOff();
        }
        return ApiResult.ok(cacheControl.isEnabled());
    }

    @DeleteMapping("/evict/{cacheName}")
    public ApiResult<String> evictCache(@PathVariable("cacheName") String cacheName, 
    		@RequestParam("cacheKeyPattern") String cacheKeyPattern,
    		@RequestParam("matchMode") String matchMode) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null && cacheManager instanceof CacheKeyManager) {
            Set<String> cacheKeys = ((CacheKeyManager) cacheManager).getCacheKeys(cacheName, cacheKeyPattern,
                    MatchMode.getBy(matchMode));
            if (CollectionUtils.isNotEmpty(cacheKeys)) {
                for (String cacheKey : cacheKeys) {
                    cache.evict(cacheKey);
                }
            }
        }
        return ApiResult.ok("Evict operation is ok");
    }

    @DeleteMapping("/evict/{cacheName}/{cacheKey}")
    public ApiResult<String> evictCache(@PathVariable("cacheName") String cacheName, 
    		                            @PathVariable("cacheKey") String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(cacheKey);
        }
        return ApiResult.ok("Evict operation is ok");
    }
    
    @DeleteMapping("/clear/{cacheName}")
    public ApiResult<String> clearCache(@PathVariable("cacheName") String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
        return ApiResult.ok("Evict operation is ok");
    }
}