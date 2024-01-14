package com.github.doodler.common.discovery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.github.doodler.common.http.RestTemplateConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;

/**
 * @Description: DiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
@AutoConfigureAfter(RestTemplateConfig.class)
@Configuration(proxyBeanMethods = false)
public class DiscoveryClientConfig {
	
    @Bean
    public SiblingApplicationInfoHeartbeatChecker siblingApplicationInfoHeartbeatChecker(ApplicationInfoManager applicationInfoManager,
                                                               Heartbeater heartbeater) {
        return new SiblingApplicationInfoHeartbeatChecker(60, 30, heartbeater, applicationInfoManager);
    }
    
    @Bean
    public SiblingApplicationInfoListener siblingApplicationInfoListener() {
    	return new SiblingApplicationInfoListener();
    }

    @Bean
    public DiscoveryClientHeartbeatChecker discoveryClientHeartbeatChecker(ApplicationInfoManager applicationInfoManager,
                                                               Heartbeater heartbeater) {
        return new DiscoveryClientHeartbeatChecker(60, 30, heartbeater, applicationInfoManager);
    }

    @Bean
    public DiscoveryClientService defaultDiscoveryClientService(DiscoveryClientHeartbeatChecker discoveryClientHeartbeatChecker) {
        return new DefaultDiscoveryClientService(discoveryClientHeartbeatChecker);
    }

    @ConditionalOnProperty(name = "discovery.client.metadata.store", havingValue = "redis", matchIfMissing = true)
    @Configuration(proxyBeanMethods = false)
    public static class RedisStoreConfig {

        @Bean
        public ApplicationInfoManager applicationInfoManager(StringRedisTemplate redisOperations,
                                                             ApplicationInfoHolder applicationInfoHolder) {
            return new RedisApplicationInfoManager(redisOperations, applicationInfoHolder);
        }

        @Profile({"dev", "test", "prod"})
        @Bean
        public ApplicationInfoRegistrar applicationInfoRegistrar(ApplicationInfoManager applicationInfoManager,
                                                                 Heartbeater heartbeater) {
            return new ApplicationInfoRegistrar(applicationInfoManager, heartbeater);
        }
    }

    @ConditionalOnProperty(name = "discovery.client.metadata.store", havingValue = "hazelcast")
    @Configuration(proxyBeanMethods = false)
    public static class HazelcastStoreConfig {

        @Value("${spring.application.cluster.name:default}")
        private String clusterName;

        @ConditionalOnMissingBean
        @Bean
        public Config hazelCastConfig() {
            Config config = new Config();
            config.setClusterName(clusterName);
            config.setInstanceName(String.format("%s-hazelcast-instance", clusterName));
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
            return config;
        }

        @Bean
        public ApplicationInfoManager applicationInfoManager(HazelcastInstance hazelcastInstance,
                                                             ApplicationInfoHolder applicationInfoHolder) {
            return new HazelcastApplicationInfoManager(hazelcastInstance, applicationInfoHolder);
        }

        @Bean
        public ApplicationInfoRegistrar applicationInfoRegistrar(ApplicationInfoManager applicationInfoManager,
                                                                 Heartbeater heartbeater) {
            return new ApplicationInfoRegistrar(applicationInfoManager, heartbeater);
        }
    }

    @Bean
    public ApplicationInfoHolder applicationInfoHolder() {
        return new ApplicationInfoHolder();
    }

    @ConditionalOnMissingBean
    @Bean
    public Heartbeater heartbeater(RestTemplate restTemplate,
                                   @Value("${discovery.client.ping.usePublicIp:false}") boolean usePublicIp) {
        return new HttpHeartbeater(restTemplate, usePublicIp);
    }
    
    @Bean
    public PrimaryApplicationInfoListener primaryApplicationInfoListener(ApplicationInfoManager applicationInfoManager,
    		ApplicationInfoHolder applicationInfoHolder) {
    	return new PrimaryApplicationInfoListener(applicationInfoManager, applicationInfoHolder);
    }
}