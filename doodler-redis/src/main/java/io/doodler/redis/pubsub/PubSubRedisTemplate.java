package io.doodler.redis.pubsub;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import io.doodler.redis.RedisSerializerUtils;

/**
 * @Description: PubSubRedisTemplate
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public class PubSubRedisTemplate extends RedisTemplate<String, Object> {

    private final String keyNamespace;
    private final String pubsubChannel;

    public PubSubRedisTemplate(RedisConnectionFactory redisConnectionFactory, String keyNamespace, String pubsubChannel) {
        super();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = RedisSerializerUtils.getJacksonRedisSerializer();
        setConnectionFactory(redisConnectionFactory);
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(jackson2JsonRedisSerializer);
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(jackson2JsonRedisSerializer);
        afterPropertiesSet();

        this.keyNamespace = keyNamespace;
        this.pubsubChannel = pubsubChannel;
    }

    private String keyFor(String channel) {
        return keyNamespace + ":" + channel;
    }

    public void convertAndUnicast(String channel, Object message) {
        String key = keyFor(channel);
        opsForList().leftPush(key, new RedisMessageEntity(channel, PubSubMode.UNICAST, message));
        super.convertAndSend(pubsubChannel, new RedisMessageEntity(channel, PubSubMode.UNICAST, null));
    }

    public void convertAndMulticast(String channel, Object message) {
        super.convertAndSend(pubsubChannel, new RedisMessageEntity(channel, PubSubMode.MULTICAST, message));
    }

    public void convertAndUnicast(String channel, Object message, long delay, TimeUnit timeUnit) {
        String key = keyFor(channel);
        opsForList().leftPush(key + "__", new RedisMessageEntity(channel, PubSubMode.UNICAST, message));
        opsForValue().set(key, new RedisMessageEntity(channel, PubSubMode.UNICAST, null), delay, timeUnit);
    }

    public void convertAndMulticast(String channel, Object message, long delay, TimeUnit timeUnit) {
        String key = keyFor(channel);
        opsForValue().set(key + "__", new RedisMessageEntity(channel, PubSubMode.MULTICAST, message));
        opsForValue().set(key, new RedisMessageEntity(channel, PubSubMode.MULTICAST, null), delay, timeUnit);
    }
}