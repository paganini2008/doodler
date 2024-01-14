package com.github.doodler.common.feign;

import feign.Request;
import feign.Response;

/**
 * @Description: RestClientInterceptor
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
public interface RestClientInterceptor {

	default void preHandle(Request request) {
	}

	default void postHandle(Request request, Response response) {
	}

	default void afterCompletion(Request request, Response response, Exception e) {
	}
}