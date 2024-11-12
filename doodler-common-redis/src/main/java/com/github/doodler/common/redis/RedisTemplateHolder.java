package com.github.doodler.common.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.github.doodler.common.redis.serial.KryoRedisSerializer;
import com.github.doodler.common.redis.serial.SnappyRedisSerializer;

/**
 * @Description: RedisTemplateHolder
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
public class RedisTemplateHolder {

    private final Jackson2JsonRedisTemplate jackson2JsonRedisTemplate;
    private final SnappyRedisTemplate snappyRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final LongRedisTemplate longRedisTemplate;
    private final IntegerRedisTemplate integerRedisTemplate;

    public RedisTemplateHolder(RedisConnectionFactory redisConnectionFactory) {
        this.jackson2JsonRedisTemplate = new Jackson2JsonRedisTemplate(redisConnectionFactory);
        this.snappyRedisTemplate = new SnappyRedisTemplate(redisConnectionFactory);
        this.stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        this.longRedisTemplate = new LongRedisTemplate(redisConnectionFactory);
        this.integerRedisTemplate = new IntegerRedisTemplate(redisConnectionFactory);
    }

    public Jackson2JsonRedisTemplate getJackson2JsonRedisTemplate() {
        return jackson2JsonRedisTemplate;
    }

    public SnappyRedisTemplate getSnappyRedisTemplate() {
        return snappyRedisTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public LongRedisTemplate getLongRedisTemplate() {
        return longRedisTemplate;
    }

    public IntegerRedisTemplate getIntegerRedisTemplate() {
        return integerRedisTemplate;
    }

    public static class IntegerRedisTemplate extends RedisTemplate<String, Integer> {

        public IntegerRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            setConnectionFactory(redisConnectionFactory);
            RedisSerializer<Integer> redisSerializer = new GenericToStringSerializer<>(Integer.class);
            setKeySerializer(RedisSerializer.string());
            setValueSerializer(redisSerializer);
            setHashKeySerializer(RedisSerializer.string());
            setHashValueSerializer(redisSerializer);
            setExposeConnection(true);
            afterPropertiesSet();
        }
    }

    public static class LongRedisTemplate extends RedisTemplate<String, Long> {

        public LongRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            setConnectionFactory(redisConnectionFactory);
            RedisSerializer<Long> redisSerializer = new GenericToStringSerializer<>(Long.class);
            setKeySerializer(RedisSerializer.string());
            setValueSerializer(redisSerializer);
            setHashKeySerializer(RedisSerializer.string());
            setHashValueSerializer(redisSerializer);
            setExposeConnection(true);
            afterPropertiesSet();
        }
    }

    public static class Jackson2JsonRedisTemplate extends RedisTemplate<String, Object> {

        public Jackson2JsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            this(redisConnectionFactory, RedisSerializerUtils.getJacksonRedisSerializer());
        }

        public Jackson2JsonRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                         Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
            super();
            setConnectionFactory(redisConnectionFactory);
            setKeySerializer(RedisSerializer.string());
            setValueSerializer(jackson2JsonRedisSerializer);
            setHashKeySerializer(RedisSerializer.string());
            setHashValueSerializer(jackson2JsonRedisSerializer);
            afterPropertiesSet();
        }
    }

    public static class SnappyRedisTemplate extends RedisTemplate<String, Object> {

        public SnappyRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            this(redisConnectionFactory, new KryoRedisSerializer<>(Object.class));
        }

        public SnappyRedisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
            super();
            setConnectionFactory(redisConnectionFactory);
            SnappyRedisSerializer<Object> snappyRedisSerializer = new SnappyRedisSerializer<>(redisSerializer);
            setKeySerializer(RedisSerializer.string());
            setValueSerializer(snappyRedisSerializer);
            setHashKeySerializer(RedisSerializer.string());
            setHashValueSerializer(snappyRedisSerializer);
            afterPropertiesSet();
        }
    }
}