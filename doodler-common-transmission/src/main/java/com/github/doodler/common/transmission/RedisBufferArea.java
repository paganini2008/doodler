package com.github.doodler.common.transmission;

import static com.github.doodler.common.Constants.ISO8601_DATE_TIME_PATTERN;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.doodler.common.utils.JacksonUtils;

/**
 * 
 * @Description: RedisBufferArea
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */

public class RedisBufferArea implements BufferArea {

    private final String namespace;
    private final RedisTemplate<String, Bucket> redisTemplate;

    public RedisBufferArea(String namespace, RedisConnectionFactory redisConnectionFactory) {
        this.namespace = namespace;
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = jsonRedisSerializer();
        RedisTemplate<String, Bucket> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        this.redisTemplate = redisTemplate;
    }

    private static Jackson2JsonRedisSerializer<Object> jsonRedisSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        om.setDateFormat(new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN));
        SimpleModule javaTimeModule = JacksonUtils.getJavaTimeModuleForWebMvc();
        om.registerModule(javaTimeModule);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<Object>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    @Override
    public void put(String collection, Bucket bucket) {
        redisTemplate.opsForList().leftPush(keyFor(collection), bucket);
    }

    private String keyFor(String collection) {
        return namespace + ":" + collection;
    }

    @Override
    public long size(String collection) {
        return redisTemplate.opsForList().size(keyFor(collection));
    }

    @Override
    public Bucket poll(String collection) {
        return redisTemplate.opsForList().leftPop(keyFor(collection));
    }

    @Override
    public Collection<Bucket> poll(String collection, int batchSize) {
        if (batchSize == 1) {
            return Collections.singletonList(poll(collection));
        }
        List<Bucket> results = redisTemplate.opsForList().leftPop(keyFor(collection), batchSize);
        return results != null && results.size() > 0 ? Collections.unmodifiableCollection(results)
                : Collections.emptyList();
    }

}
