package com.github.doodler.common.cache.filter;

import org.springframework.lang.Nullable;

/**
 * @Description: CacheMethodPostHandler
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public interface CacheMethodFilter {

    default void onPut(String cacheName, Object cacheKey) {
    }

    default void onGet(String cacheName, Object cacheKey, @Nullable Object value) {
    }

    default void onEvict(String cacheName, Object key) {
    }

    default void onClear(String cacheName) {
    }
}