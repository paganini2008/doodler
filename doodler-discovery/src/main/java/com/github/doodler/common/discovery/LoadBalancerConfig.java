package com.github.doodler.common.discovery;

import com.github.doodler.common.discovery.feign.DiscoveryClientLoadBalancerClient;
import com.github.doodler.common.feign.LoadBalancerClient;
import com.github.doodler.common.http.RestTemplateConfig;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * @Description: LoadBalancerConfig
 * @Author: Fred Feng
 * @Date: 19/12/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter(RestTemplateConfig.class)
@Configuration(proxyBeanMethods = false)
public class LoadBalancerConfig {

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