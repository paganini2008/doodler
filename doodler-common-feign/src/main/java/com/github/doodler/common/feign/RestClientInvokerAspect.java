package com.github.doodler.common.feign;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description: RestClientInvokerAspect
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public interface RestClientInvokerAspect {
	
	default boolean supports(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes) {
		return true;
	}

    void beforeInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes);

    void afterInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes, Throwable cause);
}