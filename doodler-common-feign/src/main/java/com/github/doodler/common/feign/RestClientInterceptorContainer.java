package com.github.doodler.common.feign;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description: RestClientInterceptorContainer
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class RestClientInterceptorContainer implements RequestInterceptor, ApplicationContextAware,
        SmartInitializingSingleton {

    private final List<RestClientInterceptor> interceptors = new CopyOnWriteArrayList<>();

    @Override
    public void apply(RequestTemplate template) {
        onPreHandle(template.request());
    }

    public void onPreHandle(Request request) {
        try {
            getInterceptors().forEach(interceptor -> {
                interceptor.preHandle(request);
            });
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void onPostHandle(Request request, Response response) {
        try {
            getInterceptors().forEach(interceptor -> {
                interceptor.postHandle(request, response);
            });
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void onAfterCompletion(Request request, Response response, Exception cause) {
        try {
            getInterceptors().forEach(interceptor -> {
                interceptor.afterCompletion(request, response, cause);
            });
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void addInterceptor(RestClientInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
    }

    public void removeInterceptor(RestClientInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.remove(interceptor);
        }
    }

    public List<RestClientInterceptor> getInterceptors() {
        return interceptors;
    }

    private ApplicationContext ctx;

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, RestClientInterceptor> beans = ctx.getBeansOfType(RestClientInterceptor.class);
        if (MapUtils.isNotEmpty(beans)) {
            getInterceptors().addAll(beans.values());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}