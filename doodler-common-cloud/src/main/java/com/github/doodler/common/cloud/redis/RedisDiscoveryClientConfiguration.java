package com.github.doodler.common.cloud.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 
 * @Description: RedisDiscoveryClientConfiguration
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnBlockingDiscoveryEnabled
@ConditionalOnRedisDiscoveryEnabled
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class,
        CommonsClientAutoConfiguration.class})
@AutoConfigureAfter(RedisDiscoveryAutoConfiguration.class)
public class RedisDiscoveryClientConfiguration {

    @Bean
    public RedisDiscoveryClient discoveryClient(ServiceInstanceManager serviceInstanceManager) {
        return new RedisDiscoveryClient(serviceInstanceManager);
    }
}
