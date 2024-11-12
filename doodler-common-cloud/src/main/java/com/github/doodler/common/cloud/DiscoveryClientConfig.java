package com.github.doodler.common.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: DiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@ConditionalOnDiscoveryEnabled
@Configuration(proxyBeanMethods = false)
public class DiscoveryClientConfig {

    @Bean
    public ApplicationInfoHolder applicationInfoHolder() {
        return new ApplicationInfoHolder();
    }

    @Bean("siblingDiscoveryClientChecker")
    public DiscoveryClientChecker siblingDiscoveryClientChecker(ApplicationInfoManager applicationInfoManager) {
        return new SiblingDiscoveryClientChecker(60, 30, applicationInfoManager);
    }

    @Bean
    public SiblingApplicationInfoListener siblingApplicationInfoListener() {
        return new SiblingApplicationInfoListener();
    }

    @Bean("exclusiveDiscoveryClientChecker")
    public DiscoveryClientChecker exclusiveDiscoveryClientChecker(ApplicationInfoManager applicationInfoManager) {
        return new ExclusiveDiscoveryClientChecker(60, 30, applicationInfoManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientService discoveryClientService(ApplicationInfoManager applicationInfoManager) {
        return new GenericDiscoveryClientService(applicationInfoManager);
    }

    @Bean
    public PrimaryApplicationInfoListener primaryApplicationInfoListener(ApplicationInfoManager applicationInfoManager,
                                                                         ApplicationInfoHolder applicationInfoHolder) {
        return new PrimaryApplicationInfoListener(applicationInfoManager, applicationInfoHolder);
    }

    @Bean
    public DiscoveryClient2HealthIndicator discoveryClient2HealthIndicator(DiscoveryClientService discoveryClientService) {
        return new DiscoveryClient2HealthIndicator(discoveryClientService);
    }

}