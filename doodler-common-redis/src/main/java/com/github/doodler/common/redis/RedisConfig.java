package com.github.doodler.common.redis;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Description: RedisConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(RedisAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Bean
    public RedisTemplateHolder redisTemplateHolder(RedisConnectionFactory redisConnectionFactory) {
        return new RedisTemplateHolder(redisConnectionFactory);
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisTemplateHolder redisTemplateHolder) {
        return redisTemplateHolder.getJackson2JsonRedisTemplate();
    }
}