package com.github.doodler.common.webmvc;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DefaultAsyncUncaughtExceptionHandler
 * @Author: Fred Feng
 * @Date: 11/12/2023
 * @Version 1.0.0
 */
@Slf4j
public class DefaultAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(Throwable e, Method method, Object... params) {
		if (log.isErrorEnabled()) {
			log.error("There is an unexpected exception occurred while invoking async method: {} with parameters: {}",
					method.toGenericString(), Arrays.deepToString(params), e);
		}
	}
}