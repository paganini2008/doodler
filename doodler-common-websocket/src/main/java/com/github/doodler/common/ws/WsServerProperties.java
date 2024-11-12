package com.github.doodler.common.ws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: WsServerProperties
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("ws.server")
public class WsServerProperties {

    private String securityKey;
    private long maxIdleTimeout = 60L * 1000;
    private long idleCheckInterval = 5000;
    private long idleTimeout = 10L * 1000;
    private long validityPeriod = -1;
}