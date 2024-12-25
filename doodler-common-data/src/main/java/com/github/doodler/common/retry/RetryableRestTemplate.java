package com.github.doodler.common.retry;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import com.github.doodler.common.http.RetryTemplateUtils;
import com.github.doodler.common.http.StringRestTemplate;

/**
 * @Description: RetryableRestTemplate
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public class RetryableRestTemplate extends StringRestTemplate {

    private RetryTemplate retryTemplate;

    public RetryableRestTemplate() {
        super(StandardCharsets.UTF_8);
        this.retryTemplate = RetryTemplateUtils.alwaysRetry();
    }

    public RetryableRestTemplate(int maxTimes) {
        super(StandardCharsets.UTF_8);
        this.retryTemplate = RetryTemplateUtils.retryWithMaxTimes(maxTimes);
    }

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory,
            RetryTemplate retryTemplate) {
        super(requestFactory, StandardCharsets.UTF_8);
        this.retryTemplate = retryTemplate;
    }

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory,
            RetryTemplateBuilder builder) {
        super(requestFactory, StandardCharsets.UTF_8);
        this.retryTemplate = builder != null ? builder.build() : RetryTemplateUtils.alwaysRetry();
    }

    public RetryTemplate getRetryTemplate() {
        return retryTemplate;
    }

    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    protected <T> T doExecute(URI originalUri, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        return getRetryTemplate().execute(context -> {
            return RetryableRestTemplate.super.doExecute(originalUri, method, requestCallback,
                    responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e
                    : new TerminatedRetryException(e.getMessage(), e);
        });
    }
}
