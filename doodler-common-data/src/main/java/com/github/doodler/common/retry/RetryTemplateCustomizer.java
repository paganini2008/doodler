package com.github.doodler.common.retry;

import org.springframework.retry.support.RetryTemplate;

/**
 * @Description: RetryTemplateCustomizer
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public interface RetryTemplateCustomizer {

	void customize(RetryTemplate retryTemplate);
}