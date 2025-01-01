package com.github.doodler.common.cloud.lb;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;

/**
 * 
 * @Description: LbRestTemplateHolder
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
public class LbRestTemplateHolder implements InitializingBean {

    private final RestTemplateCustomizer[] customizers;
    private final LbRestTemplate lbRestTemplate;
    private final RetryableLbRestTemplate retryableLbRestTemplate;

    public LbRestTemplateHolder(LoadBalancerClient loadBalancerClient,
            RestTemplateCustomizer... customizers) {
        this.customizers = customizers;
        this.lbRestTemplate = createNewLbRestTemplate(new LbRestTemplate());
        this.retryableLbRestTemplate = createNewLbRestTemplate(new RetryableLbRestTemplate());
    }

    public LbRestTemplate getLbRestTemplate() {
        return lbRestTemplate;
    }

    public RetryableLbRestTemplate getRetryableLbRestTemplate() {
        return retryableLbRestTemplate;
    }

    public <T extends LbRestTemplate> T createNewLbRestTemplate(T t) {
        return new RestTemplateBuilder(customizers).configure(t);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (lbRestTemplate instanceof InitializingBean) {
            ((InitializingBean) lbRestTemplate).afterPropertiesSet();
        }
        if (retryableLbRestTemplate instanceof InitializingBean) {
            ((InitializingBean) retryableLbRestTemplate).afterPropertiesSet();
        }
    }

}
