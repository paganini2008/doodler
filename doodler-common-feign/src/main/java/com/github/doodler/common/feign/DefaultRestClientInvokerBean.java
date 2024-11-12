package com.github.doodler.common.feign;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.github.doodler.common.BizException;
import com.github.doodler.common.ErrorCode;
import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DefaultRestClientInvokerBean
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Slf4j
public class DefaultRestClientInvokerBean<API> implements RestClientInvokerBean<API> {

	private final Class<API> apiInterfaceClass;
    private final API instance;
    private final List<RestClientInvokerAspect> restClientInvokerAspects;
    private final Supplier<FallbackFactory<API>> fallbackFactorySupplier;

    DefaultRestClientInvokerBean(Class<API> apiInterfaceClass,
    		                     API instance,
                                 List<RestClientInvokerAspect> restClientInvokerAspects,
                                 Supplier<FallbackFactory<API>> fallbackFactorySupplier) {
    	this.apiInterfaceClass = apiInterfaceClass;
        this.instance = instance;
        this.restClientInvokerAspects = restClientInvokerAspects;
        this.fallbackFactorySupplier = fallbackFactorySupplier;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Throwable cause = null;
        Map<String, Object> attributes = new HashMap<>();
        RequestContextHolder.currentContext().setProxy(proxy);
        RequestContextHolder.currentContext().setMethod(method);
        RequestContextHolder.currentContext().setArgs(args);
        try {
            restClientInvokerAspects.forEach(a ->{
            	if(a.supports(apiInterfaceClass,method, args, attributes)) {
            		a.beforeInvoke(apiInterfaceClass,method, args, attributes);
            	}
            });
            return method.invoke(instance, args);
        } catch (Throwable e) {
            if (fallbackFactorySupplier != null) {
                FallbackFactory<API> fallbackFactory = fallbackFactorySupplier.get();
                if (fallbackFactory != null) {
                    Object fallback = fallbackFactory.createFallback(e);
                    if (fallback != null) {
                        try {
                            return method.invoke(fallback, args);
                        } catch (Exception ee) {
                            if (log.isErrorEnabled()) {
                                log.error(ee.getMessage(), ee);
                            }
                        }
                    }
                }
            }
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) e;
                cause = ite.getTargetException();
            } else {
                cause = e;
            }

            if (cause instanceof BizException) {
                throw (BizException) cause;
            }
            if (cause instanceof RetryableException) {
                throw (RetryableException) cause;
            }
            if (cause instanceof FeignException) {
                Request currentRequest = RequestContextHolder.currentContext().getRequest();
                throw new RestClientException(currentRequest, null, ErrorCode.restClientError(cause),
                        HttpUtils.getHttpStatus((FeignException) cause), cause, null);
            }
            throw cause;
        } finally {
            Throwable copy = cause;
            restClientInvokerAspects.forEach(a -> {
            	if(a.supports(apiInterfaceClass,method, args, attributes)) {
            		a.afterInvoke(apiInterfaceClass, method, args, attributes, copy);
            	}
            });
            RequestContextHolder.clear();
        }
    }
}