package com.github.doodler.common.feign;

import java.lang.reflect.Method;

import com.alibaba.ttl.TransmittableThreadLocal;

import feign.Request;
import feign.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: RequestContextHolder
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
public class RequestContextHolder implements RestClientInterceptor {

    static final TransmittableThreadLocal<RequestContext> requestContexts = TransmittableThreadLocal.withInitial(
            () -> new RequestContext());

    public static RequestContext currentContext() {
        return requestContexts.get();
    }

    public static void clear() {
        requestContexts.remove();
    }

    @Override
    public void preHandle(Request request) {
        currentContext().setRequest(request);
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception e) {
        requestContexts.remove();
    }

    @Getter
    @Setter
    static class RequestContext {

        private Object proxy;
        private Method method;
        private Object[] args;
        private Request request;
    }
}