package com.github.doodler.common.ws.client;

/**
 * 
 * @Description: LazyReconnector
 * @Author: Fred Feng
 * @Date: 23/01/2023
 * @Version 1.0.0
 */
public class LazyReconnector implements Reconnector {

	@Override
	public boolean shouldReconnect(WsConnection connection) {
		return !connection.isAbandoned() && connection.isClosed();
	}

	@Override
	public void onSending(WsConnection connection) {
		connection.retrieve();
	}

}
