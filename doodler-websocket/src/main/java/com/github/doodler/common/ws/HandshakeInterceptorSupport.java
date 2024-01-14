package com.github.doodler.common.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * @Description: HandshakeInterceptorSupport
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
public abstract class HandshakeInterceptorSupport implements HandshakeInterceptor {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("handshake ok!");
            }
        }
    }
}