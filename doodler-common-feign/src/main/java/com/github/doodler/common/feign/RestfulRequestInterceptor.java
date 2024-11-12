package com.github.doodler.common.feign;

import static com.github.doodler.common.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @Description: RestfulRequestInterceptor
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
public class RestfulRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
    	if (!template.headers().containsKey(REQUEST_HEADER_TIMESTAMP)) {
    		template.header(REQUEST_HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    	}
        if (!template.headers().containsKey(CONTENT_TYPE)) {
            template.header(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        }
        if (!template.headers().containsKey(ACCEPT)) {
            template.header(ACCEPT, APPLICATION_JSON_VALUE);
        }
    }
}