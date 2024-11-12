package com.github.doodler.common.id;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 
 * @Description: RedisIdGeneratorFactory
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisIdGeneratorFactory implements IdGeneratorFactory {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public IdGenerator getObject() throws Exception {
        return new RedisIdGenerator(redisConnectionFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return RedisIdGenerator.class;
    }
}