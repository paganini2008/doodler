package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisOverflowDataManager
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisOverflowDataManager<T extends Metric>
        extends LoggingOverflowDataHandler<String, String, T> {

    public static final String REDIS_KEY_PATTERN = "%s:%s:%s:%s";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
    private final String namespace;
    private final RedisTemplate<String, Object> redisOperations;

    @Override
    public void persist(String category, String dimension, Instant instant, T data) {
        super.persist(category, dimension, instant, data);
        String date = instant.atZone(ZoneId.systemDefault()).toLocalDate().format(dtf);
        String key = String.format(REDIS_KEY_PATTERN, namespace, category, dimension, date);
        redisOperations.opsForHash().put(key, String.valueOf(instant.toEpochMilli()),
                data.represent());

    }

    public String getNamespace() {
        return namespace;
    }

    public RedisTemplate<String, Object> getRedisOperations() {
        return redisOperations;
    }

}
