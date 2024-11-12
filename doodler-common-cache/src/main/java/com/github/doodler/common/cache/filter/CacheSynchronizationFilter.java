package com.github.doodler.common.cache.filter;

import static com.github.doodler.common.cache.CacheConstants.PUBSUB_CHANNEL_CACHE_CHANGE_EVENT;
import com.github.doodler.common.cache.CacheChangeEvent;
import com.github.doodler.common.cache.CacheChangeType;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;

/**
 * @Description: CacheSynchronization
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
public class CacheSynchronizationFilter implements CacheMethodFilter {

    private final String applicationName;
    private final InstanceId instanceId;
    private final RedisPubSubService redisPubSubService;

    public CacheSynchronizationFilter(String applicationName, InstanceId instanceId,
                                      RedisPubSubService redisPubSubService) {
        this.applicationName = applicationName;
        this.instanceId = instanceId;
        this.redisPubSubService = redisPubSubService;
    }

    @Override
    public void onPut(String cacheName, Object cacheKey) {
        redisPubSubService.convertAndMulticast(PUBSUB_CHANNEL_CACHE_CHANGE_EVENT,
                new CacheChangeEvent(applicationName, instanceId.get(), cacheName, cacheKey, CacheChangeType.PUT));
    }

    @Override
    public void onEvict(String cacheName, Object cacheKey) {
        redisPubSubService.convertAndMulticast(PUBSUB_CHANNEL_CACHE_CHANGE_EVENT,
                new CacheChangeEvent(applicationName, instanceId.get(), cacheName, cacheKey, CacheChangeType.EVICT));
    }

    @Override
    public void onClear(String cacheName) {
        redisPubSubService.convertAndMulticast(PUBSUB_CHANNEL_CACHE_CHANGE_EVENT,
                new CacheChangeEvent(applicationName, instanceId.get(), cacheName, null, CacheChangeType.CLEAR));
    }
}