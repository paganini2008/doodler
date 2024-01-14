package com.github.doodler.common.retry;

/**
 * @Description: Retryable
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public interface Retryable {

    void retry() throws Exception;

    boolean discontinue(Exception e);
}