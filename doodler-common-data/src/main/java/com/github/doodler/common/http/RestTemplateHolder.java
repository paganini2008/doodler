package com.github.doodler.common.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.retry.RetryableRestTemplate;

/**
 * 
 * @Description: RestTemplateHolder
 * @Author: Fred Feng
 * @Date: 01/12/2024
 * @Version 1.0.0
 */
public class RestTemplateHolder {

    private final RestTemplate defaultRestTemplate;
    private final RetryableRestTemplate retryableRestTemplate;

    public RestTemplateHolder(RestTemplateCustomizer... customizers) {
        defaultRestTemplate =
                new RestTemplateBuilder(customizers).configure(new StringRestTemplate());
        retryableRestTemplate =
                new RestTemplateBuilder(customizers).configure(new RetryableRestTemplate(3));
    }

    public RestTemplate getDefaultRestTemplate() {
        return defaultRestTemplate;
    }

    public RetryableRestTemplate getRetryableRestTemplate() {
        return retryableRestTemplate;
    }

}
