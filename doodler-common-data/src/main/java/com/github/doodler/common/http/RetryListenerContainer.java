package com.github.doodler.common.http;

import static com.github.doodler.common.http.HttpRequest.CURRENT_HTTP_REQUEST;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RetryListenerContainer
 * @Author: Fred Feng
 * @Date: 19/07/2023
 * @Version 1.0.0
 */
@Slf4j
public class RetryListenerContainer implements RetryListener {

    private final List<RetryListener> retryListeners = new CopyOnWriteArrayList<RetryListener>();
    private final List<ApiRetryListener> apiRetryListeners = new CopyOnWriteArrayList<ApiRetryListener>();

    public RetryListenerContainer() {
        addListener(this);
    }

    public void addListener(ApiRetryListener listener) {
        if (listener != null) {
            apiRetryListeners.add(listener);
        }
    }

    public void removeListener(ApiRetryListener listener) {
        if (listener != null) {
            apiRetryListeners.remove(listener);
        }
    }

    public void addListener(RetryListener listener) {
        if (listener != null) {
            retryListeners.add(listener);
        }
    }

    public void removeListener(RetryListener listener) {
        if (listener != null) {
            retryListeners.remove(listener);
        }
    }

    public List<RetryListener> getRetryListeners() {
        return retryListeners;
    }

    public List<ApiRetryListener> getApiRetryListeners() {
        return apiRetryListeners;
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable e) {
    	if(e!=null) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
    	}
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable e) {
        if (context.hasAttribute(CURRENT_HTTP_REQUEST)) {
            HttpRequest request = (HttpRequest) context.getAttribute(CURRENT_HTTP_REQUEST);
            int retryCount = context.getRetryCount();
            apiRetryListeners.forEach(listener -> {
                if (listener.supports(request)) {
                    if (retryCount == 1) {
                        listener.onFirstRetry(request, e);
                    }
                    
                    listener.onRetry(request, e);

                    if (retryCount == request.getMaxAttempts()) {
                        listener.onLastRetry(request, e);
                    }
                }
            });
        }
    }
}