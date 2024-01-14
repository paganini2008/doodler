package com.github.doodler.common.cache.multilevel;

/**
 * @Description: MultiLevelCacheKeyRemovalListener
 * @Author: Fred Feng
 * @Date: 31/03/2021
 * @Version 1.0.0
 */
public interface MultiLevelCacheKeyRemovalListener {

    default void onRemovalLocalCacheKey(String cacheName, Object cacheKey, Object eldestValue) {
    }

    default void onRemovalRemoteCacheKey(String cacheName, Object cacheKey, Object eldestValue) {
    }
}