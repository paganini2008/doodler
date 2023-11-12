package io.doodler.common.discovery;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;

import io.doodler.common.discovery.actuator.DiscoveryClientHealthIndicator;
import io.doodler.common.discovery.feign.DiscoveryClientLoadBalancerClient;
import io.doodler.common.feign.LoadBalancerClient;
import io.doodler.common.http.RestTemplateConfig;

/**
 * @Description: DiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 27/03/2023
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

    @Primary
    @Bean
    public LoadBalancerClient loadBalancerClient(DiscoveryClientService discoveryClientService,
                                                 @Value("${discovery.client.ping.usePublicIp:false}") boolean usePublicIp) {
        return new DiscoveryClientLoadBalancerClient(discoveryClientService, usePublicIp);
    }

    @Bean
    public LoadBalancedRestTemplate loadBalancedRestTemplate(ClientHttpRequestFactory requestFactory,
                                                             LoadBalancerClient loadBalancerClient) {
        LoadBalancedRestTemplate restTemplate = new LoadBalancedRestTemplate(requestFactory, loadBalancerClient);
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : list) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
        return restTemplate;
    }
    
    @Bean
    public PrimaryApplicationInfoListener primaryApplicationInfoListener(ApplicationInfoManager applicationInfoManager,
    		ApplicationInfoHolder applicationInfoHolder) {
    	return new PrimaryApplicationInfoListener(applicationInfoManager, applicationInfoHolder);
    }

    @Bean
    public DiscoveryClientEndpoint discoveryClientEndpoint() {
        return new DiscoveryClientEndpoint();
    }
    
    @ConditionalOnProperty(name = "management.health.discoveryClient.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public DiscoveryClientHealthIndicator discoveryClientHealthIndicator(DiscoveryClientService discoveryClientService) {
    	return new DiscoveryClientHealthIndicator(discoveryClientService);
    }
}