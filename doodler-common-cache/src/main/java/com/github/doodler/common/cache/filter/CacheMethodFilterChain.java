package com.github.doodler.common.cache.filter;

/**
 * @Description: CacheMethodFilterChain
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class CacheMethodFilterChain implements CacheMethodFilter {

    private final CacheMethodFilter cacheMethodFilter;
    private final CacheMethodFilter nextCacheMethodFilter;

    public CacheMethodFilterChain(CacheMethodFilter cacheMethodFilter) {
        this(LoggingCacheMethodFilter.INSTANCE, cacheMethodFilter);
    }

    protected CacheMethodFilterChain(CacheMethodFilter cacheMethodFilter, CacheMethodFilter nextCacheMethodFilter) {
        this.cacheMethodFilter = cacheMethodFilter;
        this.nextCacheMethodFilter = nextCacheMethodFilter;
    }

    @Override
    public void onPut(String cacheName, Object cacheKey) {
        cacheMethodFilter.onPut(cacheName, cacheKey);
        nextCacheMethodFilter.onPut(cacheName, cacheKey);
    }

    @Override
    public void onGet(String cacheName, Object cacheKey, Object value) {
        cacheMethodFilter.onGet(cacheName, cacheKey, value);
        nextCacheMethodFilter.onGet(cacheName, cacheKey, value);
    }

    @Override
    public void onEvict(String cacheName, Object key) {
        cacheMethodFilter.onEvict(cacheName, key);
        nextCacheMethodFilter.onEvict(cacheName, key);
    }

    @Override
    public void onClear(String cacheName) {
        cacheMethodFilter.onClear(cacheName);
        nextCacheMethodFilter.onClear(cacheName);
    }

    public CacheMethodFilterChain andThen(CacheMethodFilter nextCacheMethodFilter) {
        return new CacheMethodFilterChain(this, nextCacheMethodFilter);
    }
}