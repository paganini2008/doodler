package com.github.doodler.common.jdbc;

/**
 * @Description: DataAccessException
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class DataAccessException extends RuntimeException {

	private static final long serialVersionUID = -5574281071789493929L;

	public DataAccessException(String msg, Throwable e) {
		super(msg, e);
	}

	public DataAccessException(Throwable e) {
		super(e);
	}
}