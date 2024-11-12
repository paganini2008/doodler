package com.github.doodler.common.feign;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description: NoOpRestClientInvokerAspect
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class NoOpRestClientInvokerAspect implements RestClientInvokerAspect {

	@Override
	public void beforeInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes) {
	}

	@Override
	public void afterInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes, Throwable t) {
	}

}