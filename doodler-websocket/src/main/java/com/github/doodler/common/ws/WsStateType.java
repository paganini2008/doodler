package com.github.doodler.common.ws;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;

/**
 * @Description: WsStateType
 * @Author: Fred Feng
 * @Date: 19/05/2023
 * @Version 1.0.0
 */
public enum WsStateType implements EnumConstant {

	CLOSED(0, "closed"),

	OPENED(1, "opened");

	private final int code;
	private final String repr;

	private WsStateType(int code, String repr) {
		this.code = code;
		this.repr = repr;
	}

	@Override
	@JsonValue
	public Integer getValue() {
		return code;
	}

	@Override
	public String getRepr() {
		return this.repr;
	}
}