package com.github.doodler.common.feign;

import java.lang.reflect.Proxy;

import lombok.experimental.UtilityClass;

/**
 * @Description: ApiProxyUtils
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
@UtilityClass
public class ApiProxyUtils {

	public Object getProxyInstance(Class<?> interfaceClass, RestClientInvokerBean invokerBean) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass, RestClientDeclaration.class}, invokerBean);
	}
}