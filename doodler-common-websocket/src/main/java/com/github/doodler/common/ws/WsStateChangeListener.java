package com.github.doodler.common.ws;

import java.io.IOException;
import org.springframework.lang.Nullable;

/**
 * @Description: WsStateChangeListener
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
public interface WsStateChangeListener {

    default void onOpen(WsSession session) throws IOException {
    }

    default boolean supportsMessageType(Class<?> type) {
        return true;
    }

    default void onMessage(WsUser from, Object data, long timestamp, WsSession session) throws IOException {
    }

    default void onClose(@Nullable WsSession session, int code, String reason) throws IOException {
    }
}