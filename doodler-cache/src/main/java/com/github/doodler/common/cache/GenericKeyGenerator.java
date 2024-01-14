package com.github.doodler.common.cache;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.framework.AopProxyUtils;

import cn.hutool.core.date.DateUtil;

/**
 * @Description: GenericKeyGenerator
 * @Author: Fred Feng
 * @Date: 09/02/2023
 * @Version 1.0.0
 */
public class GenericKeyGenerator implements StringKeyGenerator {

	@Override
	public String generate(String className, String methodName, Object... params) {
		String parameters = "";
		if (ArrayUtils.isNotEmpty(params)) {
			parameters = Arrays.stream(params).map(param -> retrieveKey(param)).collect(Collectors.joining(","));
		}
		return String.format(CacheConstants.DEFAULT_CACHE_KEY_PATTERN, className, methodName, parameters);
	}

	@Override
	public Object generate(Object target, Method method, Object... params) {
		String className = retrieveClassName(target, method);
		String methodName = retrieveMethodName(target, method);
		return generate(className, methodName, params);
	}

	protected String retrieveClassName(Object target, Method method) {
		return AopProxyUtils.ultimateTargetClass(target).getSimpleName();
	}

	protected String retrieveMethodName(Object target, Method method) {
		return method.getName();
	}

	protected String retrieveKey(Object arg) {
		Class<?> parameterType = arg.getClass();
		if (CharSequence.class.isAssignableFrom(parameterType) || ClassUtils.isPrimitiveOrWrapper(parameterType)
				|| Number.class.isAssignableFrom(parameterType)) {
			return arg.toString();
		} else if (Date.class.isAssignableFrom(parameterType)) {
			return DateUtil.format((Date) arg, "yyyy-MM-dd HH:mm:ss");
		} else if (LocalDateTime.class == parameterType) {
			return ((LocalDateTime) arg).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} else if (LocalDate.class == parameterType) {
			return ((LocalDate) arg).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		if (arg instanceof CacheKey) {
			return ((CacheKey) arg).getKey();
		}
		throw new IllegalArgumentException("Unable to convert as cache key by parameter: " + arg
				+ ", please try to use basic java type or implement the interface 'com.github.doodler.common.cache.CacheKey'");
	}
}