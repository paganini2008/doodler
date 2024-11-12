package com.github.doodler.common.http;

/**
 * @Description: ApiRetryListener
 * @Author: Fred Feng
 * @Date: 19/07/2023
 * @Version 1.0.0
 */
public interface ApiRetryListener {

    default boolean supports(HttpRequest request) {
        return true;
    }

    default void onFirstRetry(HttpRequest request, Throwable e) {
    }

    default void onLastRetry(HttpRequest request, Throwable e) {
    }

    default void onRetry(HttpRequest request, Throwable e) {
    }
}