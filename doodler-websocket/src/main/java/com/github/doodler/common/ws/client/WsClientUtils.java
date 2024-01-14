package com.github.doodler.common.ws.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

/**
 * @Description: WsClientUtils
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class WsClientUtils {

    public WsConnection openConnection(String url, String bearerToken) {
        return openConnection(url, bearerToken, new HashMap<>());
    }

    public WsConnection openConnection(String url, String bearerToken, Map<String, String> httpHeaders) {
        return openConnection(url, bearerToken, httpHeaders, 10000L);
    }

    public WsConnection openConnection(String url, String bearerToken, Map<String, String> httpHeaders, long connectTimeout,
                                       WsClientListener... listeners) {
        return openConnection(url, bearerToken, httpHeaders, connectTimeout, connection -> {
            connection.addListeners(listeners);
        });
    }

    public WsConnection openConnection(String url, String bearerToken, Map<String, String> httpHeaders, long connectTimeout,
                                       Consumer<WsConnection> configurer) {
        return WsConnectionBuilder.newBuilder()
                .setUrl(url)
                .setBearerToken(bearerToken)
                .setHttpHeaders(httpHeaders)
                .setConnectTimeout(connectTimeout)
                .postConfigurer(configurer)
                .build();
    }
}