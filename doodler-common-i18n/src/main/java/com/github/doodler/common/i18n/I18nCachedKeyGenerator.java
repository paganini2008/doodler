package com.github.doodler.common.i18n;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

/**
 * @Description: I18nCachedKeyGenerator
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
public class I18nCachedKeyGenerator implements KeyGenerator {

    private static final String DEFAULT_KEY_PATTERN = "%s.%s(%s)";

    @Override
    public Object generate(Object target, Method method, Object... params) {
    	final String serviceName = StringUtils.uncapitalize(method.getDeclaringClass().getSimpleName());
        String methodName = method.getName();
        if (ArrayUtils.isNotEmpty(params)) {
            String parameters = StringUtils.arrayToDelimitedString(params, ",");
            return String.format(DEFAULT_KEY_PATTERN, serviceName, methodName, parameters);
        }
        return String.format(DEFAULT_KEY_PATTERN, serviceName, methodName, "");
    }
}