package com.github.doodler.common.http;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * @Description: HttpRequest
 * @Author: Fred Feng
 * @Date: 19/07/2023
 * @Version 1.0.0
 */
public interface HttpRequest extends ExecutableHttpRequest, Cloneable {

    static final String CURRENT_HTTP_REQUEST = "currentHttpRequest";

    String getHostUrl();
    
    String getPath();
    
    String getGroup();

    HttpMethod getMethod();

    HttpHeaders getHeaders();

    Object getBody();

    Map<String, Object> getUrlVariables();

    long getTimestamp();

    int getMaxAttempts();

    Class<? extends Throwable> getRetryOn();

    int getAllowedPermits();
}