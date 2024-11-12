package com.github.doodler.common.ws.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.github.doodler.common.utils.MapUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: WsConnectionManager
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class WsConnectionManager {

    private final WsClientProperties clientConfig;
    private final Map<WsKey, WsConnection> cache = new ConcurrentHashMap<>();

    public void disconnect(String url, String bearerToken) {
        WsConnection connection = cache.remove(new WsKey(url, bearerToken));
        connection.close(false);
    }

    public void close(String url, String bearerToken) {
        WsConnection connection = cache.remove(new WsKey(url, bearerToken));
        connection.close(true);
    }

    public WsConnection getWsConnection(String url, String bearerToken) {
        final WsKey wsKey = new WsKey(url, bearerToken);
        return MapUtils.getOrCreate(cache, wsKey, () -> createWsConnection(wsKey, url, bearerToken));
    }

    private WsConnection createWsConnection(WsKey wsKey, String url, String bearerToken) {
        return WsConnectionBuilder.newBuilder().setUrl(url)
        		.setBearerToken(bearerToken)
        		.setHttpHeaders(clientConfig.getHttpHeaders())
        		.setMaxIdleTime(clientConfig.getMaxIdleTime())
                .setReconnector("eager".equals(clientConfig.getReconnectorType())
                        ? new EagerReconnector(clientConfig.getMaxRetries(), clientConfig.getRetryInterval())
                        : new LazyReconnector())
                .postConfigurer(connection -> {
                    connection.addDisposableHandler(arg -> {
                        if (((WsConnection) arg).isAbandoned()) {
                            cache.remove(wsKey);
                        }
                    });
                }).build();
    }
}