package com.github.doodler.common.discovery;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.doodler.common.discovery.actuator.DiscoveryClientHealthIndicator;

/**
 * @Description: DiscoveryClientHealthConfig
 * @Author: Fred Feng
 * @Date: 19/12/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class DiscoveryClientHealthConfig {

    @Bean
    public DiscoveryClientEndpoint discoveryClientEndpoint(DiscoveryClientService discoveryClientService) {
        return new DiscoveryClientEndpoint(discoveryClientService);
    }

    @ConditionalOnProperty(name = "management.health.discoveryClient.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public DiscoveryClientHealthIndicator discoveryClientHealthIndicator(DiscoveryClientService discoveryClientService) {
        return new DiscoveryClientHealthIndicator(discoveryClientService);
    }
}