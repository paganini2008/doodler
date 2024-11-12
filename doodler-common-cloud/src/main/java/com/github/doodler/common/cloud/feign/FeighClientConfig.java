package com.github.doodler.common.cloud.feign;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.github.doodler.common.cloud.lb.LoadBalancerClient;
import com.github.doodler.common.cloud.lb.LoadBalancerConfig;
import com.github.doodler.common.http.HttpComponentProperties;
import feign.Client;

/**
 * 
 * @Description: FeighClientConfig
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@AutoConfigureAfter({LoadBalancerConfig.class})
@ConditionalOnClass({Client.class})
@ConditionalOnDiscoveryEnabled
@Configuration(proxyBeanMethods = false)
public class FeighClientConfig {

    @Primary
    @Bean
    public StandardClientFactory standardClient(HttpComponentProperties config,
            LoadBalancerClient loadBalancerClient, BeanFactory beanFactory) {
        return new StandardClientFactory(config, loadBalancerClient, beanFactory);
    }

}
