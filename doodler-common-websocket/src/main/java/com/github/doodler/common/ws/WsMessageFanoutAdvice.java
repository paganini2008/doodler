package com.github.doodler.common.ws;

/**
 * @Description: WsMessageFanoutAdvice
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
public interface WsMessageFanoutAdvice {

	default boolean preSupports(WsUser user, Object payload) {
		return true;
	}

	default Object preFanout(WsUser user, Object payload) {
		return payload;
	}

	default boolean postSupports(WsUser from, Object data, long timestamp) {
		return true;
	}

	void postFanout(WsUser from, Object data, long timestamp, WsSession session);
}