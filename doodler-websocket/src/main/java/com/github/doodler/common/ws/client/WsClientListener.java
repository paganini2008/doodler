package com.github.doodler.common.ws.client;

/**
 * @Description: WsClientListener
 * @Author: Fred Feng
 * @Date: 08/03/2021
 * @Version 1.0.0
 */
public interface WsClientListener {

    default void onOpen(int status, String message) {
    }

    default void onMessage(String msg) {
    }

    default void onClose(int code, String reason, boolean remote) {
    }

    default void onError(Exception e) {
    }
}