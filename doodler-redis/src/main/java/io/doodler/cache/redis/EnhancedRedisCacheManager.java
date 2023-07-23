package io.doodler.cache.redis;

import static io.doodler.cache.CacheConstants.REDIS_CACHE_NAME_DELIMITER;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import io.doodler.cache.CacheKeyManager;
import io.doodler.cache.EnhancedCache;
import io.doodler.cache.filter.CacheMethodFilter;
import io.doodler.cache.multilevel.MultiLevelCacheManager;
import io.doodler.cache.spec.CacheSpecifications;
import io.doodler.common.utils.MatchMode;
import io.doodler.redis.RedisKeyIterator;

/**
 * @Description: EnhancedRedisCacheManager
 * @Author: Fred Feng
 * @Date: 09/01/2023
 * @Version 1.0.0
 * @see MultiLevelCacheManager
 */
public class EnhancedRedisCacheManager extends RedisCacheManager implements CacheKeyManager {

    public EnhancedRedisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                     RedisCacheWriter cacheWriter,
                                     RedisCacheConfiguration defaultCacheConfig,
                                     Map<String, RedisCacheConfiguration> sharedCacheConfigurations,
                                     RedisCacheConfigurationHolder redisCacheConfigurationHolder,
                                     CacheSpecifications cacheSpecifications,
                                     CacheMethodFilter cacheMethodFilter) {
        super(cacheWriter, defaultCacheConfig, sharedCacheConfigurations);

        this.redisConnectionFactory = redisConnectionFactory;
        this.redisCacheConfigurationHolder = redisCacheConfigurationHolder;
        this.cacheSpecifications = cacheSpecifications;
        this.cacheMethodFilter = cacheMethodFilter;

        setTransactionAware(true);
    }

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisCacheConfigurationHolder redisCacheConfigurationHolder;
    private final CacheSpecifications cacheSpecifications;
    private final CacheMethodFilter cacheMethodFilter;

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = super.getCache(cacheName);
        if (cache != null) {
            return cacheMethodFilter != null && cacheSpecifications.isOwner(cacheName) ?
                    EnhancedCache.createProxy(cacheName, cache, cacheMethodFilter) : cache;
        }
        return null;
    }

    @Override
    protected RedisCache getMissingCache(String cacheName) {
        RedisCacheConfiguration cacheConfig = redisCacheConfigurationHolder.getCacheConfig(cacheName);
        return cacheConfig != null ? createRedisCache(cacheName, cacheConfig) : null;
    }

    @Override
    public Set<Object> getCacheKeys(String cacheName) {
        Set<Object> cacheKeys = new HashSet<>();
        RedisCacheConfiguration cacheConfig = redisCacheConfigurationHolder.getCacheConfig(cacheName);
        String keyPrefix = cacheConfig.getKeyPrefixFor(cacheName);
        String keyPattern = keyPrefix.concat("*");
        RedisKeyIterator keyIterator = new RedisKeyIterator(keyPattern, redisConnectionFactory);
        String cacheKey;
        while (keyIterator.hasNext()) {
            cacheKey = keyIterator.next();
            cacheKeys.add(StringUtils.delimitedListToStringArray(cacheKey, REDIS_CACHE_NAME_DELIMITER)[1]);
        }
        return cacheKeys;
    }

    @Override
    public Set<String> getCacheKeys(String cacheName, String cacheKeyPattern, MatchMode matchMode) {
        Set<String> cacheKeys = new HashSet<>();
        RedisCacheConfiguration cacheConfig = redisCacheConfigurationHolder.getCacheConfig(cacheName);
        String keyPrefix = cacheConfig.getKeyPrefixFor(cacheName);
        String keyPattern = keyPrefix.concat("*");
        RedisKeyIterator keyIterator = new RedisKeyIterator(keyPattern, redisConnectionFactory);
        String cacheKey;
        while (keyIterator.hasNext()) {
            cacheKey = keyIterator.next();
            cacheKey = StringUtils.delimitedListToStringArray(cacheKey, REDIS_CACHE_NAME_DELIMITER)[1];
            if (matchMode.matches(cacheKey, cacheKeyPattern)) {
                cacheKeys.add(cacheKey);
            }
        }
        return cacheKeys;
    }
}