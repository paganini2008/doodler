package com.github.doodler.common.ws;

/**
 * @Description: WsMessageService
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
public interface WsMessageService {

    void sendObject(Object payload);

    void sendObject(Long userId, Object payload);
}