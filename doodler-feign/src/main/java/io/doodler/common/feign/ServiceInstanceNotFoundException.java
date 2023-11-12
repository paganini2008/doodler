package io.doodler.common.feign;

import org.springframework.http.HttpStatus;

import io.doodler.common.BizException;
import io.doodler.common.ErrorCode;

/**
 * @Description: ServiceInstanceNotFoundException
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public class ServiceInstanceNotFoundException extends BizException {

	private static final long serialVersionUID = -3861662687045920802L;

	public ServiceInstanceNotFoundException(String msg) {
		super(ErrorCode.restClientError(msg), HttpStatus.SERVICE_UNAVAILABLE);
	}

}