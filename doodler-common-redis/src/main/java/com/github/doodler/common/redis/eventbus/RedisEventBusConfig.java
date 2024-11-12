package com.github.doodler.common.redis.eventbus;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.redis.pubsub.RedisPubSubConfig;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;

/**
 * @Description: RedisEventBusConfig
 * @Author: Fred Feng
 * @Date: 09/03/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({RedisPubSubConfig.class})
@Configuration(proxyBeanMethods = false)
public class RedisEventBusConfig {

    @Bean
    public RedisEventBus redisEventBus(RedisPubSubService redisPubSubService) {
        return new RedisEventBus(redisPubSubService);
    }

    @Bean
    public SubscriberBeanPostProcessor subscriberBeanPostProcessor(RedisEventBus eventBus) {
        return new SubscriberBeanPostProcessor(eventBus);
    }
    
    @Bean
    public SubscriberEventPublisher subscriberEventPublisher(RedisEventBus eventBus) {
    	return new SubscriberEventPublisher(eventBus);
    }
}