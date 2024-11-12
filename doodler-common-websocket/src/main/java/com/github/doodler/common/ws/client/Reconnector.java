package com.github.doodler.common.ws.client;

/**
 * @Description: Reconnector
 * @Author: Fred Feng
 * @Date: 22/01/2023
 * @Version 1.0.0
 */
public interface Reconnector {

	default void reset() {
	}

	default boolean shouldReconnect(WsConnection connection) {
		return false;
	}

	default void onSending(WsConnection connection) {
	}

	default void onClosing(WsConnection connection) {
	}

	default void onError(WsConnection connection) {
		onClosing(connection);
	}
}