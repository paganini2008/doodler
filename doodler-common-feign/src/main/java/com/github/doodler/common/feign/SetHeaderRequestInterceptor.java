package com.github.doodler.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/**
 * @Description: SetHeaderRequestInterceptor
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public class SetHeaderRequestInterceptor implements RequestInterceptor {

    private final HttpHeaders headers;

    public SetHeaderRequestInterceptor(String headerName, String headerValue) {
        this(Collections.singletonMap(headerName, headerValue));
    }

    public SetHeaderRequestInterceptor(Map<String, String> headers) {
        this(HttpUtils.transferFrom(headers));
    }

    public SetHeaderRequestInterceptor(HttpHeaders headers) {
        Assert.notNull(headers, "HttpHeaders must not be null");
        this.headers = headers;
    }

    @Override
    public void apply(RequestTemplate template) {
        headers.entrySet().forEach(e -> {
            template.header(e.getKey(), e.getValue());
        });
    }
}