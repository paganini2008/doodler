package com.github.doodler.common.http;

import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RestClientException;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: RetryTemplateUtils
 * @Author: Fred Feng
 * @Date: 25/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class RetryTemplateUtils {

    public RetryTemplate alwaysRetry() {
        return alwaysRetry(RestClientException.class);
    }

    public RetryTemplate alwaysRetry(Class<? extends Exception> retryException) {
        return new RetryTemplateBuilder().customPolicy(new AlwaysRetryPolicy())
                .retryOn(retryException).customBackoff(new NoBackOffPolicy()).build();
    }

    public RetryTemplate retryWithMaxTimes(int maxAttempts) {
        return retryWithMaxTimes(maxAttempts, RestClientException.class);
    }

    public RetryTemplate retryWithMaxTimes(int maxAttempts,
            Class<? extends Exception> retryException) {
        return new RetryTemplateBuilder().customPolicy(new MaxAttemptsRetryPolicy(maxAttempts))
                .retryOn(retryException).customBackoff(new NoBackOffPolicy()).build();
    }

}
