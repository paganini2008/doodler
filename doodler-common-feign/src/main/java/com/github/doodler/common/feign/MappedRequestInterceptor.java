package com.github.doodler.common.feign;

import feign.RequestInterceptor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: MappedRequestInterceptor
 * @Author: Fred Feng
 * @Date: 09/06/2023
 * @Version 1.0.0
 */
public interface MappedRequestInterceptor extends RequestInterceptor {

    default boolean supports(Type type, String url, String path, Map<String, Collection<String>> headers) {
        return true;
    }
}