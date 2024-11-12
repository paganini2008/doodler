package com.github.doodler.common.cache.redis;

import static com.github.doodler.common.cache.CacheConstants.DEFAULT_REDIS_TTL_IN_SEC;
import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_PREFIX_PATTERN;
import java.time.Duration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.github.doodler.common.redis.RedisSerializerUtils;
import lombok.experimental.UtilityClass;

/**
 * @Description: RedisCacheConfigUtils
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@UtilityClass
public class RedisCacheConfigUtils {

    public RedisCacheConfiguration getDefaultCacheConfiguration(String applicationName) {
        return getDefaultCacheConfiguration(applicationName, DEFAULT_REDIS_TTL_IN_SEC);
    }

    public RedisCacheConfiguration getDefaultCacheConfiguration(String applicationName, long ttl) {
        return getCacheConfiguration(String.format(REDIS_CACHE_NAME_PREFIX_PATTERN, applicationName),
                ttl);
    }

    public RedisCacheConfiguration getCacheConfiguration(String cacheNamePrefix, long ttl) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializerUtils.getJacksonRedisSerializer()))
                .prefixCacheNameWith(cacheNamePrefix)
                .entryTtl(Duration.ofSeconds(ttl));
        return redisCacheConfiguration;
    }
}