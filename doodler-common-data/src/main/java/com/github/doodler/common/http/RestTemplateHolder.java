package com.github.doodler.common.http;

import org.springframework.beans.factory.InitializingBean;
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
public class RestTemplateHolder implements InitializingBean {

    private final RestTemplate restTemplate;
    private final RetryableRestTemplate retryableRestTemplate;
    private final RestTemplateCustomizer[] customizers;

    public RestTemplateHolder(RestTemplateCustomizer... customizers) {
        this.customizers = customizers;
        this.restTemplate = createNewRestTemplate(new RestTemplate());
        this.retryableRestTemplate = createNewRestTemplate(new RetryableRestTemplate());
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public RetryableRestTemplate getRetryableRestTemplate() {
        return retryableRestTemplate;
    }

    public <T extends RestTemplate> T createNewRestTemplate(T t) {
        return new RestTemplateBuilder(customizers).configure(t);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (retryableRestTemplate instanceof InitializingBean) {
            ((InitializingBean) retryableRestTemplate).afterPropertiesSet();
        }
    }

}
