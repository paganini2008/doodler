package com.github.doodler.common.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
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

    public RetryTemplate getRetryTemplate(int maxAttempts) {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RestClientException.class, true);
        retryableExceptions.put(IOException.class, true);
        return getRetryTemplate(maxAttempts, retryableExceptions);
    }

    public RetryTemplate getRetryTemplate(int maxAttempts,
            Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        RetryTemplate retryTemplate = new RetryTemplate();
        RetryPolicy retryPolicy =
                maxAttempts > 0 ? new SimpleRetryPolicy(maxAttempts, retryableExceptions)
                        : new NeverRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        return retryTemplate;
    }

}
