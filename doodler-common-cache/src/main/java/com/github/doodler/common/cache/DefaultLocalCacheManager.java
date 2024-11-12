package com.github.doodler.common.cache;

import static com.github.doodler.common.cache.CacheConstants.DEFAULT_CAFFEINE_CACHE_SPEC;
import static com.github.doodler.common.cache.CacheConstants.DEFAULT_CAFFEINE_TTL_IN_SEC;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.doodler.common.cache.filter.CacheMethodFilter;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.cache.spec.TtlSpec;
import com.github.doodler.common.utils.MatchMode;

/**
 * @Description: DefaultLocalCacheManager
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class DefaultLocalCacheManager extends CaffeineCacheManager implements RemovalListener<Object, Object>,
        CacheKeyManager,
        ApplicationListener<ContextRefreshedEvent> {

    private final CacheSpecifications cacheSpecifications;
    private final CacheMethodFilter cacheMethodFilter;
    private final CacheControl cacheControl;
    private final List<CacheKeyRemovalListener> cacheKeyRemovalListeners = new CopyOnWriteArrayList<>();

    public DefaultLocalCacheManager(CacheSpecifications cacheSpecifications,
                                    CacheMethodFilter cacheMethodFilter,
                                    CacheControl cacheControl) {
        this.cacheSpecifications = cacheSpecifications;
        this.cacheMethodFilter = cacheMethodFilter;
        this.cacheControl = cacheControl;
        setCaffeineSpec(CaffeineSpec.parse(getSpecification(DEFAULT_CAFFEINE_TTL_IN_SEC)));
    }

    @Override
    public Cache getCache(String cacheName) {
    	if(!cacheControl.isEnabled()) {
    		return ReadOnlyCaching.createProxy(cacheName, new NoOpCache(cacheName), cacheMethodFilter);
    	}
        Cache cache = super.getCache(cacheName);
        if (cache != null) {
        	return cacheSpecifications.isOwner(cacheName) ?
                    EnhancedCaching.createProxy(cacheName, cache, cacheMethodFilter) : 
                    	ReadOnlyCaching.createProxy(cacheName, cache, cacheMethodFilter);
        }
        return null;
    }

    @Override
    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(String cacheName) {
        long ttl = getTtl(cacheName);
        return Caffeine.from(getSpecification(ttl)).removalListener(this).build();
    }

    private long getTtl(String cacheName) {
        long ttl = DEFAULT_CAFFEINE_TTL_IN_SEC;
        TtlSpec ttlSpecification = cacheSpecifications.getTtlSpec(cacheName);
        if (ttlSpecification != null) {
            Duration duration = ttlSpecification.getTtlUnit().getDuration(ttlSpecification.getExpiration());
            ttl = duration.get(ChronoUnit.SECONDS);
        }
        return ttl;
    }

    private static String getSpecification(long ttl) {
        return String.format(DEFAULT_CAFFEINE_CACHE_SPEC, ttl);
    }

    @Override
    public Set<Object> getCacheKeys(String cacheName) {
        CaffeineCache caffeineCache = (CaffeineCache) super.getCache(cacheName);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        return Collections.unmodifiableSet(nativeCache.asMap().keySet());
    }

    @Override
    public Set<String> getCacheKeys(String cacheName, String cacheKeyPattern, MatchMode matchMode) {
        CaffeineCache caffeineCache = (CaffeineCache) super.getCache(cacheName);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        return Collections.unmodifiableSet(nativeCache.asMap().keySet().stream()
                .filter(obj -> (obj instanceof String) && matchMode.matches((String) obj, cacheKeyPattern))
                .map(obj -> (String) obj).collect(Collectors.toSet()));
    }

    public void addCacheKeyRemovalListener(CacheKeyRemovalListener cacheKeyRemovalListener) {
        if (cacheKeyRemovalListener != null) {
            cacheKeyRemovalListeners.add(cacheKeyRemovalListener);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, CacheKeyRemovalListener> beans = event.getApplicationContext().getBeansOfType(
                CacheKeyRemovalListener.class);
        if (MapUtils.isNotEmpty(beans)) {
            cacheKeyRemovalListeners.addAll(beans.values());
        }
    }

    @Override
    public void onRemoval(@Nullable Object cacheKey, @Nullable Object value, @NonNull RemovalCause cause) {
        cacheKeyRemovalListeners.forEach(cacheKeyRemovalListener -> {
            cacheKeyRemovalListener.onRemoval(null, cacheKey, value);
        });
    }
}