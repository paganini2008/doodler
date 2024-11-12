package com.github.doodler.common.cache;

import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.redis.pubsub.RedisPubSub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @Description: CacheChangeEventHandler
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CacheChangeEventHandler {

    private final InstanceId instanceId;
    private final CacheExtensionProperties config;
    private final CacheManager cacheManager;

    @Value("${spring.application.name}")
    private String applicationName;

    @RedisPubSub(CacheConstants.PUBSUB_CHANNEL_CACHE_CHANGE_EVENT)
    public void onCacheChangeEvent(String channel, Object data) {
        CacheChangeEvent cacheChangeEvent = (CacheChangeEvent) data;
        if (applicationName.equals(cacheChangeEvent.getApplicationName())
                || CollectionUtils.isEmpty(config.getSharedApplicationNames())
                || config.getSharedApplicationNames().contains(cacheChangeEvent.getApplicationName())) {
            if (instanceId.get().equals(cacheChangeEvent.getInstanceId())) {
                return;
            }

            if (cacheChangeEvent.getEventType() != null) {
                if (log.isTraceEnabled()) {
                    log.trace("[CacheChangeEvent Receiver]: {}", cacheChangeEvent);
                }
                if (cacheChangeEvent.getEventType() == CacheChangeType.PUT) {
                    cacheManager.getCache(cacheChangeEvent.getCacheName());
                } else if (cacheChangeEvent.getEventType() == CacheChangeType.EVICT) {
                    Cache cache = cacheManager.getCache(cacheChangeEvent.getCacheName());
                    if (cache instanceof MockCache) {
                        ((MockCache) cache).justEvict(cacheChangeEvent.getCacheKey());
                    } else if (cache != null) {
                        cache.evict(cacheChangeEvent.getCacheKey());
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("[CacheChangeEvent Receiver]: Evict cache by name '{}' and key '{}'",
                                cacheChangeEvent.getCacheName(), cacheChangeEvent.getCacheKey());
                    }
                } else if (cacheChangeEvent.getEventType() == CacheChangeType.CLEAR) {
                    Cache cache = cacheManager.getCache(cacheChangeEvent.getCacheName());
                    if (cache instanceof MockCache) {
                        ((MockCache) cache).justClear();
                    } else if (cache != null) {
                        cache.clear();
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("[CacheChangeEvent Receiver]: Clear cache by name '{}'",
                                cacheChangeEvent.getCacheName());
                    }
                }
            }
        }
    }
}