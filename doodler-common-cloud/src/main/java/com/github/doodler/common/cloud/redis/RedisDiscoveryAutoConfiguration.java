package com.github.doodler.common.cloud.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.DiscoveryClientRegistrar;
import com.github.doodler.common.cloud.SiblingApplicationCondition;

/**
 * 
 * @Description: RedisDiscoveryAutoConfiguration
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnRedisDiscoveryEnabled
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class RedisDiscoveryAutoConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @ConditionalOnMissingBean
    @Bean
    public Ping ping(@Value("${discovery.client.ping.usePublicIp:false}") boolean usePublicIp) {
        return new HttpPing(usePublicIp);
    }

    @Bean
    public ServiceInstanceManager defaultServiceInstanceManager(
            RedisConnectionFactory redisConnectionFactory, Ping ping,
            SiblingApplicationCondition siblingApplicationCondition) {
        return new RedisServiceInstanceManager(redisConnectionFactory, 30, ping,
                siblingApplicationCondition);
    }

    @Bean
    public ApplicationInfoManager redisApplicationInfoManager(
            ApplicationInfoHolder applicationInfoHolder,
            ServiceInstanceManager serviceInstanceManager,
            SiblingApplicationCondition siblingApplicationCondition) {
        return new RedisApplicationInfoManager(applicationName, applicationInfoHolder,
                serviceInstanceManager, siblingApplicationCondition);
    }

    @Bean
    @ConditionalOnProperty("discovery.client.instance.status.logging")
    public InstanceStatusChangeLogging instanceStatusChangeLogging() {
        return new InstanceStatusChangeLogging();
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientRegistrar discoveryClientRegistrar() {
        return new RedisDiscoveryClientRegistrar();
    }
}
