package com.github.doodler.common.cache;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * @Description: StringKeyGenerator
 * @Author: Fred Feng
 * @Date: 09/02/2023
 * @Version 1.0.0
 */
public interface StringKeyGenerator extends KeyGenerator {

	String generate(String className, String methodName, Object... params);
}