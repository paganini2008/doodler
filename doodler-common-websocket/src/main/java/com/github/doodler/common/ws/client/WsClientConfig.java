package com.github.doodler.common.ws.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: WsClientConfig
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@EnableConfigurationProperties({WsClientProperties.class})
@Configuration(proxyBeanMethods = false)
public class WsClientConfig {

    @Bean
    public WsConnectionManager wsConnectionManager(WsClientProperties wsClientProperties) {
        return new WsConnectionManager(wsClientProperties);
    }
}