package com.github.doodler.common.cache.filter;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: LoggingCacheMethodFilter
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class LoggingCacheMethodFilter implements CacheMethodFilter {

    public static final LoggingCacheMethodFilter INSTANCE = new LoggingCacheMethodFilter();

    LoggingCacheMethodFilter() {
    }

    @Override
    public void onPut(String cacheName, Object cacheKey) {
        if (log.isTraceEnabled()) {
            log.trace("[Cache Operation] type: {}, name: {}, key: {}", "put", cacheName, cacheKey);
        }
    }

    @Override
    public void onGet(String cacheName, Object cacheKey, Object value) {
        if (log.isTraceEnabled()) {
            log.trace("[Cache Operation] type: {}, name: {}, key: {}, hit: {}", "get", cacheName, cacheKey, value != null);
        }
    }

    @Override
    public void onEvict(String cacheName, Object cacheKey) {
        if (log.isTraceEnabled()) {
            log.trace("[Cache Operation] type: {}, name: {}, key: {}", "evict", cacheName, cacheKey);
        }
    }

    @Override
    public void onClear(String cacheName) {
        if (log.isTraceEnabled()) {
            log.trace("[Cache Operation] type: {}, name: {}", "clear", cacheName);
        }
    }
}