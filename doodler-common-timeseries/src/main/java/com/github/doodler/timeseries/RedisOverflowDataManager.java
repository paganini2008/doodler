package com.github.doodler.timeseries;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;

import com.github.doodler.common.context.InstanceId;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisOverflowDataManager
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisOverflowDataManager<E extends Metric> extends LoggingOverflowDataHandler<String, String, E> {

    private static final String REDIS_KEY_PATTERN = "%s:%s:%s";
    private final InstanceId instanceId;
    private final RedisOperations<String, Object> redisOperations;
    private final String namespace;

    @Override
    public void persist(String category, String dimension, Instant instant, E data) {
        super.persist(category, dimension, instant, data);

        String key = String.format(REDIS_KEY_PATTERN, instanceId.get(), category, dimension);
        if (StringUtils.isNotBlank(key)) {
            key = namespace + ":" + key;
        }
        redisOperations.opsForHash().put(key, String.valueOf(instant.toEpochMilli()), data.represent());
    }

}
