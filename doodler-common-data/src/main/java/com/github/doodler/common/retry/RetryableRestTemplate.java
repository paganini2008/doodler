package com.github.doodler.common.retry;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RetryableRestTemplate
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
@Slf4j
public class RetryableRestTemplate extends RestTemplate implements RetryListener, InitializingBean {

    private RetryTemplate retryTemplate;
    private int maxAttempts = 3;
    private Map<Class<? extends Throwable>, Boolean> retryableExceptions;

    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setRetryableExceptions(
            Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        this.retryableExceptions = retryableExceptions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (retryTemplate == null) {
            retryTemplate = createRetryTemplate();
        }
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        if (retryableExceptions == null) {
            retryableExceptions = new HashMap<>();
            retryableExceptions.put(RestClientException.class, true);
            retryableExceptions.put(IOException.class, true);
        }
        RetryPolicy retryPolicy =
                maxAttempts > 0 ? new SimpleRetryPolicy(maxAttempts, retryableExceptions)
                        : new NeverRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        retryTemplate.setListeners(new RetryListener[] {this});
        return retryTemplate;
    }

    @Override
    protected <T> T doExecute(URI originalUri, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        return retryTemplate.execute(context -> {
            return RetryableRestTemplate.super.doExecute(originalUri, method, requestCallback,
                    responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e
                    : new ExhaustedRetryException(e.getMessage(), e);
        });
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context,
            RetryCallback<T, E> callback) {
        if (log.isInfoEnabled()) {
            log.info("Start to retry. Retry count: {}", context.getRetryCount());
        }
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error("Complete to retry. Retry count: {}", context.getRetryCount(), e);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Complete to retry. Retry count: {}", context.getRetryCount());
            }
        }

    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
