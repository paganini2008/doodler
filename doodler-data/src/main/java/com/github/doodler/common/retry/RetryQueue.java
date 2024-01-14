package com.github.doodler.common.retry;

import java.util.Collection;

/**
 * @Description: RetryQueue
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
public interface RetryQueue {

    void putObject(Object object);

    void removeObject(Object object);

    int size();

    void drainTo(Collection<Object> output);
}