package com.github.doodler.common.http;

import org.springframework.web.client.RestClientException;

/**
 * @Description: FatalRestClientException
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public class FatalRestClientException extends RestClientException {

	private static final long serialVersionUID = -3905785602259333682L;

	public FatalRestClientException(String msg) {
		super(msg);
	}

	public FatalRestClientException(String msg, Throwable e) {
		super(msg, e);
	}
}