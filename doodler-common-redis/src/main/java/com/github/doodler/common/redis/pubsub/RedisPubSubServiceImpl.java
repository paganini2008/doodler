package com.github.doodler.common.redis.pubsub;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @Description: RedisPubSubServiceImpl
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public class RedisPubSubServiceImpl implements RedisPubSubService {

    private final PubSubRedisTemplate redisTemplate;

    public RedisPubSubServiceImpl(RedisConnectionFactory redisConnectionFactory, String keyNamespace, String channel) {
        this.redisTemplate = new PubSubRedisTemplate(redisConnectionFactory, keyNamespace, channel);
    }

    public RedisPubSubServiceImpl(PubSubRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void convertAndUnicast(String channel, Object message) {
        redisTemplate.convertAndUnicast(channel, message);
    }

    public void convertAndMulticast(String channel, Object message) {
        redisTemplate.convertAndMulticast(channel, message);
    }

    @Override
    public void convertAndUnicast(String channel, Object message, long delay, TimeUnit timeUnit) {
        redisTemplate.convertAndUnicast(channel, message, delay, timeUnit);
    }

    @Override
    public void convertAndMulticast(String channel, Object message, long delay, TimeUnit timeUnit) {
        redisTemplate.convertAndMulticast(channel, message, delay, timeUnit);
    }
}