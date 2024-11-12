package com.github.doodler.common.cloud.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 
 * @Description: RedisServiceRegistryAutoConfiguration
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnRedisDiscoveryEnabled
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled",
        matchIfMissing = true)
@ConditionalOnClass({RedisConnectionFactory.class})
@AutoConfigureAfter({AutoServiceRegistrationConfiguration.class,
        AutoServiceRegistrationAutoConfiguration.class, RedisDiscoveryAutoConfiguration.class})
public class RedisServiceRegistryAutoConfiguration {

    @Bean
    public RedisServiceRegistry redisServiceRegistry(
            ServiceInstanceManager serviceInstanceManager) {
        return new RedisServiceRegistry(serviceInstanceManager);
    }

    @Bean
    public ServiceRegistrationFactoryBean serviceRegistration() {
        return new ServiceRegistrationFactoryBean();
    }

    @Bean
    public RedisAutoServiceRegistry redisAutoServiceRegistry(
            RedisServiceRegistry redisServiceRegistry,
            AutoServiceRegistrationProperties autoServiceRegistrationProperties,
            ServiceRegistration serverRegistration) {
        return new RedisAutoServiceRegistry(redisServiceRegistry, autoServiceRegistrationProperties,
                serverRegistration, null);
    }
}
