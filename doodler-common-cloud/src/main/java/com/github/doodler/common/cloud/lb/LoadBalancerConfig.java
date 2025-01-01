package com.github.doodler.common.cloud.lb;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import com.github.doodler.common.cloud.DiscoveryClientConfig;
import com.github.doodler.common.cloud.DiscoveryClientLoadBalancerClient;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.http.LoggingHttpRequestInterceptor;
import com.github.doodler.common.http.RestTemplateConfig;

/**
 * @Description: LoadBalancerConfig
 * @Author: Fred Feng
 * @Date: 19/12/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({RestTemplateConfig.class, DiscoveryClientConfig.class})
@ConditionalOnDiscoveryEnabled
@Configuration(proxyBeanMethods = false)
public class LoadBalancerConfig {

    @ConditionalOnMissingBean
    @Bean
    public LoadBalancerClient loadBalancerClient(DiscoveryClientService discoveryClientService,
            @Value("${discovery.client.ping.usePublicIp:false}") boolean usePublicIp) {
        return new DiscoveryClientLoadBalancerClient(discoveryClientService, usePublicIp, true);
    }

    @Bean
    public LbRestTemplateCustomizer lbRestTemplateCustomizer(
            ClientHttpRequestFactory clientHttpRequestFactory,
            LoadBalancerClient loadBalancerClient, RetryTemplate retryTemplate) {
        return new LbRestTemplateCustomizer(clientHttpRequestFactory,
                Arrays.asList(new LoggingHttpRequestInterceptor()), loadBalancerClient,
                retryTemplate);
    }

    @Bean
    public LbRestTemplateHolder lbRestTemplateHolder(LoadBalancerClient loadBalancerClient,
            @Qualifier("lbRestTemplateCustomizer") RestTemplateCustomizer customizer) {
        return new LbRestTemplateHolder(loadBalancerClient, customizer);
    }

    @Bean
    public LbRestTemplate lbRestTemplate(LbRestTemplateHolder lbRestTemplateHolder) {
        return lbRestTemplateHolder.getLbRestTemplate();
    }
}
