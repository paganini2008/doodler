package com.github.doodler.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: RequestInterceptorContainer
 * @Author: Fred Feng
 * @Date: 09/06/2023
 * @Version 1.0.0
 */
public class RequestInterceptorContainer implements RequestInterceptor, ApplicationContextAware,
        SmartInitializingSingleton {

    private final List<MappedRequestInterceptor> interceptors = new CopyOnWriteArrayList<>();

    @Override
    public void apply(RequestTemplate template) {
        Type currentType = template.feignTarget().type();
        String url = template.feignTarget().url();
        for (MappedRequestInterceptor interceptor : interceptors) {
            if (interceptor.supports(currentType, url, template.url(), template.headers())) {
                interceptor.apply(template);
            }
        }
    }

    public void addInterceptor(MappedRequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            interceptors.add(requestInterceptor);
        }
    }

    public void removeInterceptor(MappedRequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            interceptors.remove(requestInterceptor);
        }
    }

    public List<MappedRequestInterceptor> getInterceptors() {
        return interceptors;
    }

    private ApplicationContext ctx;

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, MappedRequestInterceptor> beans = ctx.getBeansOfType(MappedRequestInterceptor.class);
        if (MapUtils.isNotEmpty(beans)) {
            beans.values().forEach(i -> addInterceptor(i));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}