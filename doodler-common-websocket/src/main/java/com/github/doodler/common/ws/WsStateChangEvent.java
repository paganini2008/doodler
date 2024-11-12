package com.github.doodler.common.ws;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: WsStateChangEvent
 * @Author: Fred Feng
 * @Date: 19/05/2023
 * @Version 1.0.0
 */
public class WsStateChangEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4112082950039987809L;

	public WsStateChangEvent(Object source, WsStateType stateType) {
		super(source);
		this.stateType = stateType;
	}

	private final WsStateType stateType;

	public WsStateType getStateType() {
		return stateType;
	}
}