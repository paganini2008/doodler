package com.github.doodler.common.utils;

/**
 * @Description: AsyncOperationException
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
public class AsyncOperationException extends RuntimeException {

	private static final long serialVersionUID = -2125600260940941099L;

	public AsyncOperationException(String msg) {
		super(msg);
	}

	public AsyncOperationException(String msg, Throwable e) {
		super(msg, e);
	}
}