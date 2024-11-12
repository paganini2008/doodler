package com.github.doodler.common.ws.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import com.github.doodler.common.retry.RetryQueue;
import com.github.doodler.common.retry.SimpleRetryQueue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Description: WsConnectionBuilder
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Accessors(chain = true)
@Getter
@Setter
public class WsConnectionBuilder {

    private String url;
    private Map<String, String> httpHeaders;
    private long connectTimeout;
    private long maxIdleTime;
    private Reconnector reconnector;
    private String bearerToken;
    private RetryQueue retryQueue;
    private List<WsClientListener> listeners;
    private Consumer<WsConnection> configurer;

    WsConnectionBuilder() {
        connectTimeout = 10000L;
        reconnector = new EagerReconnector(5000L);
        retryQueue = new SimpleRetryQueue();
    }

    public WsConnectionBuilder postConfigurer(Consumer<WsConnection> configurer) {
        if (configurer != null) {
            this.configurer = (this.configurer != null ? this.configurer.andThen(configurer) : configurer);
        }
        return this;
    }

    public static WsConnectionBuilder newBuilder() {
        return new WsConnectionBuilder();
    }

    public WsConnection build() {
        Assert.hasText(url, "WebSocket url must be required");
        if (httpHeaders == null) {
            httpHeaders = new HashMap<>();
        }
        if (StringUtils.isNotBlank(bearerToken)) {
            httpHeaders.put(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }
        WsConnection connection = createWsConnection();
        if (reconnector != null) {
            connection.setReconnector(reconnector);
        }
        if (retryQueue != null) {
            connection.setRetryQueue(retryQueue);
        }
        if (maxIdleTime > 0) {
            connection.setMaxIdleTime(maxIdleTime);
        }
        if (configurer != null) {
            configurer.accept(connection);
        }
        connection.connect(connectTimeout);
        return connection;
    }
    
    protected WsConnection createWsConnection() {
		return new WsConnectionImpl(url, httpHeaders);
	}
}