package com.github.doodler.common.cache.redis;

import static com.github.doodler.common.cache.CacheConstants.DEFAULT_CACHE_SYNC_INTERVAL;
import static com.github.doodler.common.cache.CacheConstants.DEFAULT_REDIS_TTL_IN_SEC;
import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_PREFIX_PATTERN;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import com.github.doodler.common.cache.CacheExtensionProperties;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.cache.spec.TtlSpec;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.enums.AppName;
import com.github.doodler.common.redis.RedisSerializerUtils;
import com.github.doodler.common.utils.ExecutorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RedisCacheConfigurationHolder
 * @Author: Fred Feng
 * @Date: 01/02/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheConfigurationHolder implements Runnable, ManagedBeanLifeCycle,
        ApplicationListener<ApplicationReadyEvent> {

    private final CacheExtensionProperties config;
    private final CacheSpecifications cacheSpecifications;
    private final RedisCacheLoader redisCacheLoader;

    private ScheduledExecutorService internalScheduledExecutor;
    private ScheduledFuture<?> scheduledFuture;

    @Value("${spring.application.name}")
    private String applicationName;

    private final ConcurrentMap<String, RedisCacheConfiguration> stableCacheConfigs = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> unstableCacheSharers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RedisCacheConfiguration> unstableCacheConfigs = new ConcurrentHashMap<>();

    public @Nullable RedisCacheConfiguration getCacheConfig(String cacheName) {
        RedisCacheConfiguration cacheConfig = null;
        if (cacheSpecifications.isOwner(cacheName)) {
            cacheConfig = getCacheCondfigIfAbsent(stableCacheConfigs, cacheName, applicationName);
        } else if (cacheSpecifications.isSharer(cacheName)) {
            cacheConfig = getCacheCondfigIfAbsent(stableCacheConfigs, cacheName,
                    cacheSpecifications.getSharedApplicationName(cacheName));
        } else {
            synchronized (unstableCacheSharers) {
                cacheConfig = getCacheCondfigIfAbsent(unstableCacheConfigs, cacheName, unstableCacheSharers.get(cacheName));
            }
        }
        return cacheConfig;
    }

    private RedisCacheConfiguration getCacheCondfigIfAbsent(Map<String, RedisCacheConfiguration> map, String cacheName,
                                                            String applicationName) {
        if (StringUtils.isBlank(applicationName) || StringUtils.equalsIgnoreCase("null", applicationName)) {
            return null;
        }
        RedisCacheConfiguration cacheConfig = map.get(cacheName);
        if (cacheConfig == null) {
            map.putIfAbsent(cacheName, getRedisCacheConfiguration(applicationName, cacheName));
            cacheConfig = map.get(cacheName);
        }
        return cacheConfig;
    }
    
    private RedisCacheConfiguration getRedisCacheConfiguration(String applicationName, String cacheName) {
    	String cacheNamePrefix = String.format(REDIS_CACHE_NAME_PREFIX_PATTERN, applicationName);
    	long ttl = getTtl(cacheName);
    	RedisSerializer<Object> valueSerializer = getValueSerializer(cacheName);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .prefixCacheNameWith(cacheNamePrefix)
                .entryTtl(Duration.ofSeconds(ttl));
        return redisCacheConfiguration;
    }

    private long getTtl(String cacheName) {
        long ttl = DEFAULT_REDIS_TTL_IN_SEC;
        TtlSpec ttlSpec = cacheSpecifications.getTtlSpec(cacheName);
        if (ttlSpec != null) {
            Duration duration = ttlSpec.getTtlUnit().getDuration(ttlSpec.getExpiration());
            ttl = duration.get(ChronoUnit.SECONDS);
        }
        return ttl + RandomUtils.nextInt(1, 20);
    }
    
    private RedisSerializer<Object> getValueSerializer(String cacheName){
    	RedisSerializer<Object> redisSerializer  = cacheSpecifications.getValueSerializer(cacheName);
    	if(redisSerializer == null) {
    		redisSerializer = RedisSerializerUtils.getJacksonRedisSerializer();
    	}
    	return redisSerializer;
    }

    @Override
    public void run() {
        Set<String> applicationNames = CollectionUtils.isNotEmpty(config.getSharedApplicationNames())
                ? new HashSet<>(config.getSharedApplicationNames())
                : Arrays.stream(AppName.values()).map(app -> app.getValue()).collect(Collectors.toSet());
        // applicationNames.remove(applicationName); ??

        Map<String, String> cacheNames = new HashMap<>();
        applicationNames.forEach(applicationName -> {
            Set<String> names = redisCacheLoader.getCacheNames(
                    applicationName, config.getExcludedCacheNames());
            names.forEach(name -> {
                cacheNames.put(name, applicationName);
            });
        });
        Map<String, String> lastCopy = new HashMap<>(unstableCacheSharers);
        synchronized (unstableCacheSharers) {
            unstableCacheSharers.clear();
            unstableCacheConfigs.clear();
            unstableCacheSharers.putAll(cacheNames);
        }
        if (log.isInfoEnabled()) {
            Collection<String> effectedCacheNames = CollectionUtils.disjunction(lastCopy.keySet(), cacheNames.keySet());
            if (CollectionUtils.isNotEmpty(effectedCacheNames)) {
                log.info("Discovery effected cache names: {}", effectedCacheNames);
            }
            Collection<String> subtractedCacheNames = CollectionUtils.subtract(lastCopy.keySet(), cacheNames.keySet());
            if (CollectionUtils.isNotEmpty(subtractedCacheNames)) {
                log.info("Discovery discarded cache names: {}", subtractedCacheNames);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (internalScheduledExecutor == null) {
            internalScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        }

        int checkInterval = DEFAULT_CACHE_SYNC_INTERVAL;
        scheduledFuture = internalScheduledExecutor.scheduleWithFixedDelay(this, checkInterval, checkInterval,
                TimeUnit.SECONDS);
        if (log.isInfoEnabled()) {
            log.info("Keeping track of new cache with {} seconds frequency after {} seconds.", checkInterval,
                    checkInterval);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        ExecutorUtils.gracefulShutdown(internalScheduledExecutor, 60000L);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        run();
    }
}