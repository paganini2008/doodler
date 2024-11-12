package com.github.doodler.common.ws.client;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WsClientWrapper
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class WsClientWrapper extends WebSocketClient {

    WsClientWrapper(URI serverUri, Map<String, String> httpHeaders, WsClientListener... listeners) {
        super(serverUri, httpHeaders);
        this.clientListeners.addAll(Arrays.asList(listeners));
    }

    private final List<WsClientListener> clientListeners = new CopyOnWriteArrayList<>();

    public void addListeners(WsClientListener ... listeners) {
        if (listeners != null) {
            clientListeners.addAll(Arrays.asList(listeners));
        }
    }

    public void removeListener(WsClientListener listener) {
        if (listener != null) {
            clientListeners.remove(listener);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (log.isInfoEnabled()) {
            log.info("[OnOpen] status: {}, message: {}", handshakedata.getHttpStatus(),
                    handshakedata.getHttpStatusMessage());
        }
        clientListeners.forEach(handler -> {
            handler.onOpen(handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
        });
    }

    @Override
    public void onMessage(String message) {
        if (log.isTraceEnabled()) {
            log.trace("[OnMessage] - message: {}", message);
        }
        clientListeners.forEach(handler -> {
            handler.onMessage(message);
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (log.isInfoEnabled()) {
            log.info("[OnClose] - code: {}, reason: {}, remote: {}", code, reason, remote);
        }
        clientListeners.forEach(handler -> {
            handler.onClose(code, reason, remote);
        });
    }

    @Override
    public void onError(Exception e) {
        if (log.isErrorEnabled()) {
            log.error("[OnError] - " + e.getMessage(), e);
        }
        clientListeners.forEach(handler -> {
            handler.onError(e);
        });
    }
}