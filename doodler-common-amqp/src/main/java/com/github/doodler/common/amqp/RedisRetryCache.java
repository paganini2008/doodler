package com.github.doodler.common.amqp;

import cn.hutool.core.collection.CollectionUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Description: RedisRetryCache
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisRetryCache implements RetryCache {

    private final String key;
    private final RedisOperations<String, Object> redisOperations;

    public RedisRetryCache(String key, RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.java());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.java());
        redisTemplate.afterPropertiesSet();
        this.redisOperations = redisTemplate;
        this.key = key;
    }

    @Override
    public boolean putObject(String id, CachedObject cachedObject) {
        redisOperations.opsForHash().put(key, id, cachedObject);
        return true;
    }

    @Override
    public CachedObject getObject(String id) {
        return (CachedObject) redisOperations.opsForHash().get(key, id);
    }

    @Override
    public CachedObject removeObject(String id) {
        CachedObject cachedObject = getObject(id);
        if (cachedObject != null) {
            redisOperations.opsForHash().delete(key, id);
        }
        return cachedObject;
    }

    @Override
    public Collection<String> remainingIds() {
        Set<Object> ids = redisOperations.opsForHash().keys(key);
        return CollectionUtil.isNotEmpty(ids) ? ids.stream().map(Object::toString).collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Override
    public int size() {
        Number result = redisOperations.opsForHash().size(key);
        return result != null ? result.intValue() : 0;
    }
}