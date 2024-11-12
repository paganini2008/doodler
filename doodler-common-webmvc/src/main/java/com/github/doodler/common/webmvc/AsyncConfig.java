package com.github.doodler.common.webmvc;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * @Description: AsyncConfig
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

	private final TaskExecutor taskExecutor;

	@Override
	public Executor getAsyncExecutor() {
		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return asyncUncaughtExceptionHandler();
	}

	@ConditionalOnMissingBean
	@Bean
	public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
		return new DefaultAsyncUncaughtExceptionHandler();
	}
	
}