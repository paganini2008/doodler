package com.github.doodler.common.cache;

import com.github.doodler.common.annotations.TtlUnit;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.utils.MatchMode;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * @Description: CacheUtils
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
@SuppressWarnings("unchecked")
@Component
public class CacheUtils {

    private static CacheManager cacheManager;
    private static CacheSpecifications cacheSpecifications;

    @Autowired
    public void setCacheManager(CacheManager cacheManager, CacheSpecifications cacheSpecifications) {
        CacheUtils.cacheManager = cacheManager;
        CacheUtils.cacheSpecifications = cacheSpecifications;
    }

    public static <T> T getCache(String cacheName, Object cacheKey) {
        return getCache(cacheName, cacheKey, null);
    }

    public static <T> T getCache(String cacheName, Object cacheKey, T defaultValue) {
        Cache cache = cacheManager.getCache(cacheName);
        ValueWrapper valueWrapper = cache != null ? cache.get(cacheKey) : null;
        return valueWrapper != null ? (T) valueWrapper.get() : defaultValue;
    }

    public static void putCache(String cacheName, Object cacheKey, Object cacheValue) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(cacheKey, cacheValue);
        }
    }

    public static void putCache(String cacheName, Object cacheKey, Object cacheValue, long ttl, TtlUnit ttlUnit) {
    	cacheSpecifications.addCacheName(cacheName);
        cacheSpecifications.addTtlSpec(ttl, ttlUnit, cacheName);
        putCache(cacheName, cacheKey, cacheValue);
    }

    public static void evictCache(String cacheName, Object cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(cacheKey);
        }
    }

    public static void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public static void clearCache(String cacheName, String cacheKeyPattern, MatchMode matchMode) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null && cacheManager instanceof CacheKeyManager) {
            Set<String> cacheKeys = ((CacheKeyManager) cacheManager).getCacheKeys(cacheName, cacheKeyPattern, matchMode);
            if (CollectionUtils.isNotEmpty(cacheKeys)) {
                for (String cacheKey : cacheKeys) {
                    cache.evict(cacheKey);
                }
            }
        }
    }

    public static String genericCacheKey(String className, String methodName, Object... arguments) {
        return CacheKeyBuilder.generic().setClassName(className).setMethodName(methodName).addArguments(arguments).build();
    }

    public static String genericCacheKey(Class<?> type, String methodName, Object... arguments) {
        return genericCacheKey(type.getSimpleName(), methodName, arguments);
    }
}