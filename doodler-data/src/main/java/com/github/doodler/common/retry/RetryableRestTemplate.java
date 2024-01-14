package com.github.doodler.common.retry;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.RetryListener;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.doodler.common.http.FatalRestClientException;

/**
 * @Description: RetryableRestTemplate
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */

public class RetryableRestTemplate extends RestTemplate implements InitializingBean {

    private RetryTemplate retryTemplate;
    private int maxRetryCount;
    private Class<? extends RestClientException> retryOnClass;
    private Class<? extends RestClientException> notRetryOnClass;
    private BackOffPolicy backOffPolicy;
    private final List<RetryListener> retryListeners = new CopyOnWriteArrayList<>();

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        this.maxRetryCount = -1;
        this.retryOnClass = RestClientException.class;
        this.notRetryOnClass = FatalRestClientException.class;
        this.backOffPolicy = new NoBackOffPolicy();
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public List<RetryListener> getRetryListeners() {
        return retryListeners;
    }

    public Class<? extends RestClientException> getRetryOnClass() {
        return retryOnClass;
    }

    public void setRetryOnClass(Class<? extends RestClientException> retryOnClass) {
        this.retryOnClass = retryOnClass;
    }

    public Class<? extends RestClientException> getNotRetryOnClass() {
        return notRetryOnClass;
    }

    public void setNotRetryOnClass(Class<? extends RestClientException> notRetryOnClass) {
        this.notRetryOnClass = notRetryOnClass;
    }

    public BackOffPolicy getBackOffPolicy() {
        return backOffPolicy;
    }

    public void setBackOffPolicy(BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
    }

    public RetryTemplate getRetryTemplate() {
        return retryTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        retryTemplate = new RetryTemplateBuilder()
                .customPolicy(maxRetryCount > 0 ? new MaxAttemptsRetryPolicy(maxRetryCount) : new AlwaysRetryPolicy())
                .retryOn(retryOnClass)
                .notRetryOn(notRetryOnClass)
                .customBackoff(backOffPolicy)
                .withListeners(retryListeners).build();
    }

    @Override
    protected <T> T doExecute(URI originalUri, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) {
        return retryTemplate.execute(context -> {
            return RetryableRestTemplate.super.doExecute(originalUri, method, requestCallback, responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e :
                    new TerminatedRetryException(e.getMessage(), e);
        });
    }
}