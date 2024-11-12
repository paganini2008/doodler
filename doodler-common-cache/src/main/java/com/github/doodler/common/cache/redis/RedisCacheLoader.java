package com.github.doodler.common.cache.redis;

import static com.github.doodler.common.cache.CacheConstants.DEFAULT_REDIS_TTL_IN_SEC;
import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_DELIMITER;
import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_PREFIX;
import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_PREFIX_PATTERN;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.cache.spec.TtlSpec;
import com.github.doodler.common.redis.RedisKeyIterator;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RedisCacheLoader
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class RedisCacheLoader {

    private static final Charset DEFAULT = StandardCharsets.UTF_8;
    private final RedisConnectionFactory connectionFactory;
    private final Duration sleepTime;
    private final CacheStatisticsCollector statistics;
    private final CacheSpecifications cacheSpecifications;

    public RedisCacheLoader(RedisConnectionFactory connectionFactory, CacheSpecifications cacheSpecifications) {
        this(connectionFactory, Duration.ZERO, cacheSpecifications);
    }

    public RedisCacheLoader(RedisConnectionFactory connectionFactory, Duration sleepTime,
                            CacheSpecifications cacheSpecifications) {
        this(connectionFactory, sleepTime, CacheStatisticsCollector.none(), cacheSpecifications);
    }

    public RedisCacheLoader(RedisConnectionFactory connectionFactory, Duration sleepTime,
                            CacheStatisticsCollector statistics,
                            CacheSpecifications cacheSpecifications) {
        this.connectionFactory = connectionFactory;
        this.sleepTime = sleepTime;
        this.statistics = statistics;
        this.cacheSpecifications = cacheSpecifications;
    }

    public Map<String, RedisCacheConfiguration> getSharedCacheConfigurations(
            Collection<String> excludedCacheNames) {
        Map<String, RedisCacheConfiguration> configurations = new LinkedHashMap<>();
        getCacheNames(excludedCacheNames).entrySet().forEach(e -> {
            RedisCacheConfiguration redisCacheConfiguration = getRedisCacheConfiguration(e.getValue(), e.getKey());
            configurations.put(e.getKey(), redisCacheConfiguration);
            if (log.isInfoEnabled()) {
                log.info("[Following application: {}] Initialize cache configuration: {} --> {}", e.getValue(), e.getKey(),
                        redisCacheConfiguration);
            }
        });
        return configurations;
    }

    public Map<String, RedisCacheConfiguration> getSharedCacheConfigurations(String applicationName,
                                                                             Collection<String> excludedCacheNames) {
        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
        getCacheNames(applicationName, excludedCacheNames).forEach(cacheName -> {
            RedisCacheConfiguration redisCacheConfiguration = getRedisCacheConfiguration(applicationName, cacheName);
            configurations.put(cacheName, redisCacheConfiguration);
            if (log.isInfoEnabled()) {
                log.info("[Following application: {}] Initialize cache configuration: {} --> {}", applicationName,
                        cacheName,
                        redisCacheConfiguration);
            }
        });
        return configurations;
    }

    public Map<String, String> getCacheNames(Collection<String> excludedCacheNames) {
        Set<String> fullCacheNames = getFullCacheNames(REDIS_CACHE_NAME_PREFIX);
        Map<String, String> cacheNames = fullCacheNames.stream().map(fullCacheName -> fullCacheName.split(":")).collect(
                HashMap::new, (m, e) -> m.put(e[1], e[0]), HashMap::putAll);
        if (CollectionUtils.isNotEmpty(excludedCacheNames)) {
            return cacheNames.entrySet().stream().filter(
                            e -> excludedCacheNames == null || !excludedCacheNames.contains(e.getKey()))
                    .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
        }
        return cacheNames;
    }

    public Set<String> getCacheNames(String applicationName, Collection<String> excludedCacheNames) {
        String keyPrefix = String.format(REDIS_CACHE_NAME_PREFIX_PATTERN, applicationName);
        Set<String> cacheNames = getCacheNames(keyPrefix);
        if (CollectionUtils.isNotEmpty(excludedCacheNames)) {
            return cacheNames.stream().filter(
                            cacheName -> excludedCacheNames == null || !excludedCacheNames.contains(cacheName))
                    .collect(Collectors.toSet());
        }
        return cacheNames;
    }

    public Set<String> getCacheNames(final String keyPrefix) {
        final String keyPattern = keyPrefix.concat("*");
        Set<String> cacheNames = new HashSet<>();
        RedisKeyIterator keyIterator = new RedisKeyIterator(keyPattern, connectionFactory);
        String cacheKey;
        while (keyIterator.hasNext()) {
            cacheKey = keyIterator.next();
            if (cacheKey.contains(REDIS_CACHE_NAME_DELIMITER)) {
                cacheNames.add(StringUtils.delimitedListToStringArray(cacheKey, REDIS_CACHE_NAME_DELIMITER)[0].replace(
                        keyPrefix, ""));
            }
        }
        return cacheNames;
    }

    public Set<String> getFullCacheNames(final String keyPrefix) {
        final String keyPattern = keyPrefix.concat("*");
        Set<String> cacheNames = new HashSet<>();
        RedisKeyIterator keyIterator = new RedisKeyIterator(keyPattern, connectionFactory);
        String cacheKey;
        while (keyIterator.hasNext()) {
            cacheKey = keyIterator.next();
            if (cacheKey.contains(REDIS_CACHE_NAME_DELIMITER)) {
                cacheNames.add(StringUtils.delimitedListToStringArray(cacheKey, REDIS_CACHE_NAME_DELIMITER)[0]);
            }
        }
        return cacheNames;
    }

    private RedisCacheConfiguration getRedisCacheConfiguration(String applicationName, String cacheName) {
        return RedisCacheConfigUtils.getDefaultCacheConfiguration(
                String.format(REDIS_CACHE_NAME_PREFIX_PATTERN, applicationName),
                getTtl(cacheName));
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

    void executeLockFree(Consumer<RedisConnection> callback) {
        RedisConnection connection = connectionFactory.getConnection();
        try {
            callback.accept(connection);
        } finally {
            connection.close();
        }
    }

    <T> T execute(String name, Function<RedisConnection, T> callback) {
        RedisConnection connection = connectionFactory.getConnection();
        try {
            checkAndPotentiallyWaitUntilUnlocked(name, connection);
            return callback.apply(connection);
        } finally {
            connection.close();
        }
    }

    private boolean isLockingCacheWriter() {
        return !sleepTime.isZero() && !sleepTime.isNegative();
    }

    private boolean doCheckLock(String name, RedisConnection connection) {
        return connection.exists(createCacheLockKey(name));
    }

    private void checkAndPotentiallyWaitUntilUnlocked(String name, RedisConnection connection) {
        if (!isLockingCacheWriter()) {
            return;
        }
        long lockWaitTimeNs = System.nanoTime();
        try {
            while (doCheckLock(name, connection)) {
                Thread.sleep(sleepTime.toMillis());
            }
        } catch (InterruptedException ex) {
            // Re-interrupt current thread, to allow other participants to react.
            Thread.currentThread().interrupt();

            throw new PessimisticLockingFailureException(
                    String.format("Interrupted while waiting to unlock cache %s", name), ex);
        } finally {
            statistics.incLockTime(name, System.nanoTime() - lockWaitTimeNs);
        }
    }

    private static byte[] createCacheLockKey(String name) {
        return (name + "~lock").getBytes(DEFAULT);
    }
}