package com.github.doodler.common.feign;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @Description: BearerAuthRequestInterceptor
 * @Author: Fred Feng
 * @Date: 23/11/2022
 * @Version 1.0.0
 */
public class BearerAuthRequestInterceptor implements RequestInterceptor {

    private static final String REQUEST_HEADER_AUTHORIZATION_PREFIX = "Bearer ";
    private final String authorization;

    public BearerAuthRequestInterceptor(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public void apply(RequestTemplate template) {
        String bearer = authorization;
        if (!bearer.startsWith(REQUEST_HEADER_AUTHORIZATION_PREFIX)) {
            bearer = REQUEST_HEADER_AUTHORIZATION_PREFIX + bearer;
        }
        template.header(AUTHORIZATION, bearer);
    }
}