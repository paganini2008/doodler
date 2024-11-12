package com.github.doodler.common.cache.multilevel;

import com.github.doodler.common.cache.CacheKeyManager;
import com.github.doodler.common.cache.EnhancedCaching;
import com.github.doodler.common.cache.filter.CacheMethodFilter;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.utils.MatchMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

/**
 * @Description: MultiLevelCacheManager
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class MultiLevelCacheManager extends AbstractCacheManager implements CacheKeyManager {

    private final CacheManager localCacheManger;
    private final CacheManager remoteCacheManager;
    private final CacheSpecifications cacheSpecifications;
    private final CacheMethodFilter cacheMethodFilter;

    public MultiLevelCacheManager(CacheManager localCacheManager,
                                  CacheManager remoteCacheManager,
                                  CacheSpecifications cacheSpecifications,
                                  CacheMethodFilter cacheMethodFilter) {
        this.localCacheManger = localCacheManager;
        this.remoteCacheManager = remoteCacheManager;
        this.cacheSpecifications = cacheSpecifications;
        this.cacheMethodFilter = cacheMethodFilter;
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = super.getCache(cacheName);
        if (cache != null) {
            return cacheMethodFilter != null && cacheSpecifications.isOwner(cacheName) ?
                    EnhancedCaching.createProxy(cacheName, cache, cacheMethodFilter) : cache;
        }
        return null;
    }

    @Override
    protected Cache getMissingCache(String cacheName) {
        return createCache(cacheName);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<String> remoteCacheNames = new HashSet<>(remoteCacheManager.getCacheNames());
        List<Cache> caches = new ArrayList<>();
        remoteCacheNames.forEach(name -> {
            Cache cache = createCache(name);
            if (cache != null) {
                caches.add(cache);
            }
        });
        return caches;
    }

    private Cache createCache(String cacheName) {
        Cache localCache = localCacheManger.getCache(cacheName);
        Cache remoteCache = remoteCacheManager.getCache(cacheName);
        if (localCache == null || remoteCache == null) {
            return null;
        }
        return new MultiLevelCache(cacheName, localCache,
                remoteCache);
    }

    @Override
    public Set<Object> getCacheKeys(String cacheName) {
        Set<Object> allCacheKeys = new HashSet<>();
        if (localCacheManger instanceof CacheKeyManager) {
            allCacheKeys.addAll(((CacheKeyManager) localCacheManger).getCacheKeys(cacheName));
        }
        if (remoteCacheManager instanceof CacheKeyManager) {
            allCacheKeys.addAll(((CacheKeyManager) remoteCacheManager).getCacheKeys(cacheName));
        }
        return allCacheKeys;
    }

    @Override
    public Set<String> getCacheKeys(String cacheName, String cacheKeyPattern, MatchMode matchMode) {
        Set<String> allCacheKeys = new HashSet<>();
        if (localCacheManger instanceof CacheKeyManager) {
            allCacheKeys.addAll(((CacheKeyManager) localCacheManger).getCacheKeys(cacheName, cacheKeyPattern, matchMode));
        }
        if (remoteCacheManager instanceof CacheKeyManager) {
            allCacheKeys.addAll(((CacheKeyManager) remoteCacheManager).getCacheKeys(cacheName, cacheKeyPattern, matchMode));
        }
        return allCacheKeys;
    }
}