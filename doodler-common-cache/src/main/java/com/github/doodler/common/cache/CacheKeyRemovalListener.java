package com.github.doodler.common.cache;

import org.springframework.lang.Nullable;

/**
 * @Description: CacheKeyRemovalListener
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public interface CacheKeyRemovalListener {

    void onRemoval(@Nullable String cacheName, Object cacheKey, @Nullable Object cacheValue);
}