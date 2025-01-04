package com.github.doodler.common.retry;

import java.util.Arrays;
import java.util.concurrent.Callable;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.stereotype.Component;

/**
 * @Description: RetryOperations
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
@Component
public class RetryOperations {

    public Object execute(Runnable task, int maxRetryCount, long interval,
            Class<? extends Throwable>[] retryOnClasses, RetryListener... retryListeners)
            throws Exception {
        RetryTemplate retryTemplate = new RetryTemplateBuilder().maxAttempts(maxRetryCount)
                .fixedBackoff(interval).retryOn(Arrays.asList(retryOnClasses))
                .withListeners(Arrays.asList(retryListeners)).build();
        return execute(task, retryTemplate);
    }

    public Object execute(Runnable task, RetryPolicy retryPolicy, BackOffPolicy backOffPolicy,
            Class<? extends Throwable>[] retryOnClasses, RetryListener... retryListeners)
            throws Exception {
        RetryTemplate retryTemplate = new RetryTemplateBuilder().customPolicy(retryPolicy)
                .customBackoff(backOffPolicy).retryOn(Arrays.asList(retryOnClasses))
                .withListeners(Arrays.asList(retryListeners)).build();
        return execute(task, retryTemplate);
    }

    public Object execute(Runnable task, RetryTemplate retryTemplate) throws Exception {
        return retryTemplate.execute(context -> {
            task.run();
            return null;
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw new ExhaustedRetryException(e.getMessage(), e);
        });
    }

    public <T> T execute(Callable<T> task, int maxRetryCount, long interval,
            Class<? extends Throwable>[] retryOnClasses, RetryListener... retryListeners)
            throws Exception {
        RetryTemplate retryTemplate = new RetryTemplateBuilder().maxAttempts(maxRetryCount)
                .fixedBackoff(interval).retryOn(Arrays.asList(retryOnClasses))
                .withListeners(Arrays.asList(retryListeners)).build();
        return execute(task, retryTemplate);
    }

    public <T> T execute(Callable<T> task, RetryPolicy retryPolicy, BackOffPolicy backOffPolicy,
            Class<? extends Throwable>[] retryOnClasses, RetryListener... retryListeners)
            throws Exception {
        RetryTemplate retryTemplate = new RetryTemplateBuilder().customPolicy(retryPolicy)
                .customBackoff(backOffPolicy).retryOn(Arrays.asList(retryOnClasses))
                .withListeners(Arrays.asList(retryListeners)).build();
        return execute(task, retryTemplate);
    }

    public <T> T execute(Callable<T> task, RetryTemplate retryTemplate) throws Exception {
        return retryTemplate.execute(context -> {
            return task.call();
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw new ExhaustedRetryException(e.getMessage(), e);
        });
    }
}
