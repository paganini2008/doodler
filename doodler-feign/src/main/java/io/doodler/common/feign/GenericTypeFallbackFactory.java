package io.doodler.common.feign;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: GenericTypeFallbackFactory
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class GenericTypeFallbackFactory<API> implements FallbackFactory<API>, MethodInterceptor {

	private final Class<API> apiInterfaceClass;

	public GenericTypeFallbackFactory(Class<API> apiInterfaceClass) {
		this.apiInterfaceClass = apiInterfaceClass;
	}

	private API restClientProxy;

	@Override
	public API createFallback(Throwable e) {
		if (e != null) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
		if (restClientProxy == null) {
			restClientProxy = createProxyObject();
		}
		return restClientProxy;
	}

	@SuppressWarnings("unchecked")
	private API createProxyObject() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(apiInterfaceClass);
		enhancer.setCallback(this);
		return (API) enhancer.create();
	}

	@Override
	public final Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		final String methodName = method.getName();
		if (methodName.equals("equals")) {
			return false;
		} else if (methodName.equals("hashcode")) {
			return System.identityHashCode(this);
		} else if (methodName.equals("toString")) {
			return super.toString();
		}
		return invokeNullableMethod(apiInterfaceClass, proxy, method, args);
	}

	protected Object invokeNullableMethod(Class<?> restClientType, Object proxy, Method method, Object[] args) {
		return null;
	}
}