package com.github.doodler.common.feign;

import feign.RetryableException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: RetryFailureHandlerContainer
 * @Author: Fred Feng
 * @Date: 27/09/2023
 * @Version 1.0.0
 */
@Slf4j
public class RetryFailureHandlerContainer implements RetryFailureHandler, ApplicationContextAware,
        SmartInitializingSingleton {

    private final List<RetryFailureHandler> handlers = new CopyOnWriteArrayList<>();

    public void addHandler(RetryFailureHandler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    public void removeHandler(RetryFailureHandler handler) {
        if (handler != null) {
            handlers.remove(handler);
        }
    }

    public List<RetryFailureHandler> getHandlers() {
        return handlers;
    }

    private ApplicationContext ctx;

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, RetryFailureHandler> beans = ctx.getBeansOfType(RetryFailureHandler.class);
        if (MapUtils.isNotEmpty(beans)) {
            beans.values().stream().filter(i -> i != this).forEach(i -> addHandler(i));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public void onRetryFailed(RetryableException reason) {
        for (RetryFailureHandler handler : getHandlers()) {
            try {
                handler.onRetryFailed(reason);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}