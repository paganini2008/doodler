package com.github.doodler.common.cloud.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.composite.reactive.ReactiveCompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: RedisReactiveDiscoveryClientConfiguration
 * @Author: Fred Feng
 * @Date: 25/12/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnReactiveDiscoveryEnabled
@ConditionalOnRedisDiscoveryEnabled
@AutoConfigureAfter({ReactiveCompositeDiscoveryClientAutoConfiguration.class,
        RedisDiscoveryAutoConfiguration.class})
@AutoConfigureBefore(ReactiveCommonsClientAutoConfiguration.class)
public class RedisReactiveDiscoveryClientConfiguration {

    @Bean
    public RedisReactiveDiscoveryClient reactiveDiscoveryClient(
            ServiceInstanceManager serviceInstanceManager) {
        return new RedisReactiveDiscoveryClient(serviceInstanceManager);
    }

}
