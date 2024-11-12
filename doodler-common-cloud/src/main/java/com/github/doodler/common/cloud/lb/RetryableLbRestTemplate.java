package com.github.doodler.common.cloud.lb;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

/**
 * 
 * @Description: RetryableLbRestTemplate
 * @Author: Fred Feng
 * @Date: 27/07/2024
 * @Version 1.0.0
 */
public class RetryableLbRestTemplate extends LoadBalancedRestTemplate {

    public RetryableLbRestTemplate(ClientHttpRequestFactory requestFactory, LoadBalancerClient loadBalancerClient,
                                   RetryTemplate retryTemplate) {
        super(requestFactory, loadBalancerClient);
        this.retryTemplate = retryTemplate;
    }

    public RetryableLbRestTemplate(ClientHttpRequestFactory requestFactory, LoadBalancerClient loadBalancerClient,
                                   RetryTemplateBuilder builder) {
        super(requestFactory, loadBalancerClient);
        this.retryTemplate = builder != null ? builder.build() : getDefault();
    }

    private RetryTemplate retryTemplate;

    public RetryTemplate getRetryTemplate() {
        return retryTemplate;
    }

    private static RetryTemplate getDefault() {
        return new RetryTemplateBuilder().customPolicy(new AlwaysRetryPolicy()).retryOn(
                RestClientException.class).customBackoff(new NoBackOffPolicy()).build();
    }

    @Override
    protected <T> T doExecute(URI originalUri, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException {
        return getRetryTemplate().execute(context -> {
            return RetryableLbRestTemplate.super.doExecute(originalUri, method, requestCallback, responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e :
                    new TerminatedRetryException(e.getMessage(), e);
        });
    }

}
