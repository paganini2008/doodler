package com.github.doodler.common.http;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: HttpRequestBuilder
 * @Author: Fred Feng
 * @Date: 19/07/2023
 * @Version 1.0.0
 */
public class HttpRequestBuilder {

	private RestTemplate restTemplate;
	private String hostUrl;
	private String group;
	private HttpMethod method;
	private HttpHeaders headers;
	private int maxAttempts;
	private int allowedPermits;
	private Class<? extends Throwable> retryOn;
	private Object body;
	private Map<String, Object> urlVariables;

	public HttpRequestBuilder() {
		this.restTemplate = new RestTemplate();
		this.headers = new HttpHeaders();
		this.maxAttempts = 0;
		this.allowedPermits = -1;
		this.retryOn = Exception.class;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	public HttpRequestBuilder setHostUrl(String hostUrl) {
		this.hostUrl=hostUrl;
		return this;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public HttpRequestBuilder setMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpRequestBuilder setHeaders(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public Object getBody() {
		return body;
	}

	public HttpRequestBuilder setBody(Object body) {
		this.body = body;
		return this;
	}

	public Map<String, Object> getUrlVariables() {
		return urlVariables;
	}

	public HttpRequestBuilder setUrlVariables(Map<String, Object> urlVariables) {
		this.urlVariables = urlVariables;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public HttpRequestBuilder setGroup(String group) {
		this.group = group;
		return this;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public HttpRequestBuilder setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}

	public int getAllowedPermits() {
		return allowedPermits;
	}

	public HttpRequestBuilder setAllowedPermits(int allowedPermits) {
		this.allowedPermits = allowedPermits;
		return this;
	}

	public Class<? extends Throwable> getRetryOn() {
		return retryOn;
	}

	public HttpRequestBuilder setRetryOn(Class<? extends Throwable> retryOn) {
		this.retryOn = retryOn;
		return this;
	}

	public HttpRequest build() {
		BasicHttpRequest httpRequest = new BasicHttpRequest(hostUrl, method, restTemplate);
		httpRequest.setGroup(group);
		httpRequest.setMaxAttempts(maxAttempts);
		httpRequest.setAllowedPermits(allowedPermits);
		httpRequest.setRetryOn(retryOn);
		httpRequest.setBody(body);
		httpRequest.setUrlVariables(urlVariables);
		return httpRequest;
	}

	public static HttpRequestBuilder get() {
		return new HttpRequestBuilder().setMethod(HttpMethod.GET);
	}

	public static HttpRequestBuilder post() {
		return new HttpRequestBuilder().setMethod(HttpMethod.POST);
	}

	public static HttpRequestBuilder put() {
		return new HttpRequestBuilder().setMethod(HttpMethod.PUT);
	}

	public static HttpRequestBuilder delete() {
		return new HttpRequestBuilder().setMethod(HttpMethod.DELETE);
	}
}