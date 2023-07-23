package io.doodler.discovery;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import io.doodler.common.utils.RestTemplateConfig;
import io.doodler.discovery.feign.DiscoveryClientLoadBalancerClient;
import io.doodler.feign.LoadBalancerClient;


/**
 * @Description: DiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter(RestTemplateConfig.class)
@Configuration(proxyBeanMethods = false)
public class DiscoveryClientConfig {

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientService discoveryClientService(StringRedisTemplate redisOperations,
                                                         PingStrategy pingStrategy) {
        return new DefaultDiscoveryClientService(redisOperations, pingStrategy);
    }

    @Profile({"dev", "test", "prod"})
    @Bean
    public DiscoveryClientRegistrar discoveryClientRegistrar(
            StringRedisTemplate redisOperations, PingStrategy pingStrategy) {
        return new DiscoveryClientRegistrar(redisOperations, pingStrategy);
    }

    @ConditionalOnMissingBean
    @Bean
    public PingStrategy pingStrategy(RestTemplate restTemplate,
                                     @Value("${discovery.client.ping.usePublicIp:false}") boolean usePublicIp) {
        return new UrlPingStrategy(restTemplate, usePublicIp);
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
}