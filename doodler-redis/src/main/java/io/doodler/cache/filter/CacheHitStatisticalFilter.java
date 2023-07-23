package io.doodler.cache.filter;

import io.doodler.cache.CacheHitStatisticsCollector;

/**
 * @Description: CacheHitStatisticalFilter
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class CacheHitStatisticalFilter implements CacheMethodFilter {

    private final CacheHitStatisticsCollector cacheHitStatisticsCollector;

    public CacheHitStatisticalFilter(CacheHitStatisticsCollector cacheHitStatisticsCollector) {
        this.cacheHitStatisticsCollector = cacheHitStatisticsCollector;
    }

    @Override
    public void onGet(String cacheName, Object cacheKey, Object value) {
        if (value != null) {
            cacheHitStatisticsCollector.statistics(cacheName).incrHits();
        }
        cacheHitStatisticsCollector.statistics(cacheName).incrTotal();
    }

    @Override
    public void onEvict(String cacheName, Object key) {
        cacheHitStatisticsCollector.statistics(cacheName).incrEvicts();
    }

    @Override
    public void onPut(String cacheName, Object cacheKey) {
        cacheHitStatisticsCollector.statistics(cacheName).incrPuts();
    }
}