package com.github.doodler.common.webmvc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: WebResponsePreHandlerContainer
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class WebResponsePreHandlerContainer implements ResponseBodyAdvice<Object>, SmartInitializingSingleton,
        ApplicationContextAware {

    private final List<WebResponsePreHandler> preHandlers = new CopyOnWriteArrayList<>();
    private ApplicationContext applicationContext;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        Object reference = body;
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        for (WebResponsePreHandler preHandler : preHandlers) {
            try {
                if (reference != null && preHandler.supports(reference.getClass(), httpRequest, httpResponse)) {
                    reference = preHandler.beforeBodyWrite(reference, httpRequest, httpResponse);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return reference;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, WebResponsePreHandler> beans = applicationContext.getBeansOfType(WebResponsePreHandler.class);
        if (MapUtils.isNotEmpty(beans)) {
            preHandlers.addAll(beans.values());
            preHandlers.sort(AnnotationAwareOrderComparator.INSTANCE);
        }
    }
}