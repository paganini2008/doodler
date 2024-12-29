package com.github.doodler.common.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({DiscoveryClientProperties.class})
@Configuration(proxyBeanMethods = false)
public class DiscoveryClientConfig {

    @Autowired
    private DiscoveryClientProperties discoveryClientProperties;

    @Bean
    public ApplicationInfoHolder applicationInfoHolder() {
        return new ApplicationInfoHolder();
    }

    @ConditionalOnMissingBean(name = "siblingDiscoveryClientChecker")
    @Bean("siblingDiscoveryClientChecker")
    public DiscoveryClientChecker siblingDiscoveryClientChecker(
            ApplicationInfoManager applicationInfoManager) {
        return new SiblingDiscoveryClientChecker(60, 30,
                discoveryClientProperties.getSibling().isQuickStart(), applicationInfoManager);
    }

    @Bean
    public SiblingApplicationInfoListener siblingApplicationInfoListener() {
        return new SiblingApplicationInfoListener();
    }

    @ConditionalOnMissingBean(name = "exclusiveDiscoveryClientChecker")
    @Bean("exclusiveDiscoveryClientChecker")
    public DiscoveryClientChecker exclusiveDiscoveryClientChecker(
            ApplicationInfoManager applicationInfoManager) {
        return new ExclusiveDiscoveryClientChecker(60, 30,
                discoveryClientProperties.getExclusive().isQuickStart(), applicationInfoManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientService discoveryClientService(
            ApplicationInfoManager applicationInfoManager) {
        return new GenericDiscoveryClientService(applicationInfoManager);
    }

    @Bean
    public PrimaryApplicationInfoListener primaryApplicationInfoListener(
            ApplicationInfoManager applicationInfoManager,
            ApplicationInfoHolder applicationInfoHolder) {
        return new PrimaryApplicationInfoListener(applicationInfoManager, applicationInfoHolder);
    }

    @Bean
    public MetadataCollector appInfoMetadataCollector(ApplicationInfoHolder applicationInfoHolder) {
        return new ApplicationInfoMetadataCollector(applicationInfoHolder);
    }

    @Bean
    public DiscoveryClient2HealthIndicator discoveryClient2HealthIndicator(
            DiscoveryClientService discoveryClientService) {
        return new DiscoveryClient2HealthIndicator(discoveryClientService);
    }

}
