package com.github.doodler.common.retry;

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
import org.springframework.web.client.RestTemplate;

/**
 * @Description: RetryableRestTemplate
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public class RetryableRestTemplate extends RestTemplate {

    private final RetryTemplate retryTemplate;

    public RetryableRestTemplate() {
        super();
        this.retryTemplate = getDefault();
    }

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory, RetryTemplate retryTemplate) {
        super(requestFactory);
        this.retryTemplate = retryTemplate;
    }

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory, RetryTemplateBuilder builder) {
        super(requestFactory);
        this.retryTemplate = builder != null ? builder.build() : getDefault();
    }

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
            return RetryableRestTemplate.super.doExecute(originalUri, method, requestCallback, responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e :
                    new TerminatedRetryException(e.getMessage(), e);
        });
    }
}