package com.github.doodler.common.ws.client;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: WsClientProperties
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("ws.client")
public class WsClientProperties {

    private int maxRetries = -1;
    private long retryInterval = 5000L;
    private long maxIdleTime = 0;
    private String reconnectorType = "eager";
    private Map<String, String> httpHeaders;
}