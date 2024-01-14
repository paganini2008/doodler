package com.github.doodler.common.retry;

/**
 * @Description: RetryableRestTemplateFactory
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public interface RetryableRestTemplateFactory {

    RetryableRestTemplate createRestTemplate(RetryTemplateCustomizer... customizers);
}