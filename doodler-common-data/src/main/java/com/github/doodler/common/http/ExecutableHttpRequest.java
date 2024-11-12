package com.github.doodler.common.http;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryListener;

/**
 * @Description: ExecutableHttpRequest
 * @Author: Fred Feng
 * @Date: 20/07/2023
 * @Version 1.0.0
 */
public interface ExecutableHttpRequest {

	void addRetryListener(RetryListener listener);

	void addApiRetryListener(ApiRetryListener listener);

	<T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders, Object requestBody, Class<T> responseType);

	<T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders, Map<String, Object> urlVariables,
	                              Class<T> responseType);

	<T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders, Object requestBody,
	                              ParameterizedTypeReference<T> typeReference);

	<T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders, Map<String, Object> urlVariables,
	                              ParameterizedTypeReference<T> typeReference);

	default <T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders, Class<T> responseType) {
		return execute(url, httpHeaders, Collections.emptyMap(), responseType);
	}

	default <T> ResponseEntity<T> execute(String url, HttpHeaders httpHeaders,
	                                      ParameterizedTypeReference<T> typeReference) {
		return execute(url, httpHeaders, Collections.emptyMap(), typeReference);
	}
}