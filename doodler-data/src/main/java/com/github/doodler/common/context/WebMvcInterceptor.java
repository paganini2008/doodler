package com.github.doodler.common.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * 
 * @Description: WebMvcInterceptor
 * @Author: Fred Feng
 * @Date: 20/05/2020
 * @Version 1.0.0
 */
public interface WebMvcInterceptor {
	
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	}

	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception e) throws Exception {
	}
}
