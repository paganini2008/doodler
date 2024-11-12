package com.github.doodler.common.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: HttpRequestInfo
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class HttpRequestInfo implements Serializable {

    private static final long serialVersionUID = 5359161967793485293L;
    private String requestId;
    private String method;
    private String path;
    private HttpStatus status;
    private HttpHeaders requestHeaders = new HttpHeaders();
    private @Nullable String requestBody;
    private HttpHeaders responseHeaders = new HttpHeaders();
    private @Nullable Object responseBody;
    private long timestmap;
    private String[] errors;

    private @Nullable AuthInfo authInfo;
    private Map<String, Object> attributes = new HashMap<>();

    public HttpRequestInfo() {
    }

    public HttpRequestInfo(String method, String path, HttpHeaders headers) {
        this(method, path, headers, null);
    }

    public HttpRequestInfo(String method, String path, HttpHeaders requestHeaders, String requestBody) {
        this.method = method;
        this.path = path;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.timestmap = System.currentTimeMillis();
    }

    public void addRequestHeaders(MultiValueMap<String, String> headers) {
        if (headers != null) {
            requestHeaders.addAll(headers);
        }
    }

    public void addResponseHeaders(MultiValueMap<String, String> headers) {
        if (headers != null) {
            responseHeaders.addAll(headers);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class AuthInfo {

        private Object principal;
        private @Nullable String role;
        private @Nullable String permission;
        private boolean approved;
    }
}