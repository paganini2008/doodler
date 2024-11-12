package com.github.doodler.common.http;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryListener;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: BasicHttpRequest
 * @Author: Fred Feng
 * @Date: 19/07/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public class BasicHttpRequest implements HttpRequest {

	private final String hostUrl;
	private final HttpMethod method;
	private final HttpRequestExecutor httpRequestExecutor;

	BasicHttpRequest(String hostUrl, HttpMethod method, RestTemplate restTemplate) {
		this(hostUrl, method, new DefaultHttpRequestExecutor(restTemplate));
	}

	BasicHttpRequest(String hostUrl, HttpMethod method, HttpRequestExecutor httpRequestExecutor) {
		this.hostUrl = hostUrl;
		this.method = method;
		this.httpRequestExecutor = httpRequestExecutor;
		this.maxAttempts = 0;
		this.allowedPermits = -1;
		this.retryOn = Exception.class;
	}

	private String path;
	private String group;
	private HttpHeaders headers;
	private Object body;
	private Map<String, Object> urlVariables;
	private int maxAttempts;
	private Class<? extends Throwable> retryOn;
	private int allowedPermits;
	private long timestamp;
	
	@Override
	public void addRetryListener(RetryListener listener) {
		this.httpRequestExecutor.addRetryListener(listener);
	}

	@Override
	public void addApiRetryListener(ApiRetryListener listener) {
		this.httpRequestExecutor.addApiRetryListener(listener);
	}

	@Override
	public <T> ResponseEntity<T> execute(String path, HttpHeaders httpHeaders, Object body, Class<T> responseType) {
		BasicHttpRequest httpRequest = this.clone();
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpRequest.setPath(path);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(body);
		httpRequest.setTimestamp(System.currentTimeMillis());
		return httpRequestExecutor.perform(httpRequest, responseType);
	}

	@Override
	public <T> ResponseEntity<T> execute(String path, HttpHeaders httpHeaders, Map<String, Object> urlVariables, Class<T> responseType) {
		BasicHttpRequest httpRequest = this.clone();
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpRequest.setPath(path);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setUrlVariables(urlVariables);
		httpRequest.setTimestamp(System.currentTimeMillis());
		return httpRequestExecutor.perform(httpRequest, responseType);
	}

	@Override
	public <T> ResponseEntity<T> execute(String path, HttpHeaders httpHeaders, Object requestBody,
	                                     ParameterizedTypeReference<T> typeReference) {
		BasicHttpRequest httpRequest = this.clone();
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpRequest.setPath(path);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(requestBody);
		httpRequest.setTimestamp(System.currentTimeMillis());
		return httpRequestExecutor.perform(httpRequest, typeReference);
	}

	@Override
	public <T> ResponseEntity<T> execute(String path, HttpHeaders httpHeaders, Map<String, Object> urlVariables,
	                                     ParameterizedTypeReference<T> typeReference) {
		BasicHttpRequest httpRequest = this.clone();
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpRequest.setPath(path);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setUrlVariables(urlVariables);
		httpRequest.setTimestamp(System.currentTimeMillis());
		return httpRequestExecutor.perform(httpRequest, typeReference);
	}

	@Override
	protected BasicHttpRequest clone() {
		try {
			return (BasicHttpRequest) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public String toString() {
		return method.name().toUpperCase() + " " + hostUrl;
	}


}