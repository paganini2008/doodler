package com.github.doodler.common.feign;

import feign.RetryableException;

/**
 * @Description: RetryFailureHandler
 * @Author: Fred Feng
 * @Date: 27/09/2023
 * @Version 1.0.0
 */
public interface RetryFailureHandler {

	void onRetryFailed(RetryableException reason);
}