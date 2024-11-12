package com.github.doodler.common.http;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryListener;

/**
 * @Description: HttpRequestExecutor
 * @Author: Fred Feng
 * @Date: 20/07/2023
 * @Version 1.0.0
 */
public interface HttpRequestExecutor {
	
	void addRetryListener(RetryListener listener);
	
	void addApiRetryListener(ApiRetryListener listener);

    <T> ResponseEntity<T> perform(HttpRequest httpRequest, Class<T> responseType);

    <T> ResponseEntity<T> perform(HttpRequest httpRequest, ParameterizedTypeReference<T> typeReference);
}