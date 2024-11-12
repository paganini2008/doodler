package com.github.doodler.common.retry;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;

/**
 * @Description: GenericRetryableRestTemplateFactory
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public class GenericRetryableRestTemplateFactory implements RetryableRestTemplateFactory {

    private final RestTemplateCustomizer[] restTemplateCustomizers;

    public GenericRetryableRestTemplateFactory(RestTemplateCustomizer... restTemplateCustomizers) {
        this.restTemplateCustomizers = restTemplateCustomizers;
    }

    @Override
    public RetryableRestTemplate createRestTemplate(RetryTemplateCustomizer... customizers) {
        RetryableRestTemplate retryableRestTemplate = new RestTemplateBuilder(restTemplateCustomizers).build(
                RetryableRestTemplate.class);
        if (ArrayUtils.isNotEmpty(customizers)) {
            for (RetryTemplateCustomizer customizer : customizers) {
                customizer.customize(retryableRestTemplate.getRetryTemplate());
            }
        }
        return retryableRestTemplate;
    }
}