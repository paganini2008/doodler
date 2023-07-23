package io.doodler.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.doodler.common.utils.MapUtils;

/**
 * @Description: CacheHitStatisticsCollector
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class CacheHitStatisticsCollector {

    private final Map<String, CacheHitStatistics> cache = new ConcurrentHashMap<>();

    public CacheHitStatistics statistics(String cacheName) {
        return MapUtils.getOrCreate(cache, cacheName, CacheHitStatistics::new);
    }

    public boolean hasCacheName(String cacheName) {
        return cache.containsKey(cacheName);
    }

    public int countOfCacheNames() {
        return cache.size();
    }

    public Map<String, CacheHitStatistics> statistics() {
        return new HashMap<>(cache);
    }
}