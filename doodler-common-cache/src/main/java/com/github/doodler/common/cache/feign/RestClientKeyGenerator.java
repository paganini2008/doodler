package com.github.doodler.common.cache.feign;

import java.lang.reflect.Method;
import com.github.doodler.common.cache.GenericKeyGenerator;

/**
 * @Description: RestClientKeyGenerator
 * @Author: Fred Feng
 * @Date: 01/02/2023
 * @Version 1.0.0
 */
public class RestClientKeyGenerator extends GenericKeyGenerator {

	@Override
	protected String retrieveClassName(Object target, Method method) {
		return method.getDeclaringClass().getSimpleName();
	}

}