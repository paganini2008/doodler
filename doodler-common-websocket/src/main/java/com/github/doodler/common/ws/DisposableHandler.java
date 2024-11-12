package com.github.doodler.common.ws;

/**
 * @Description: DisposableHandler
 * @Author: Fred Feng
 * @Date: 23/01/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface DisposableHandler {

    void dispose(Object target);
}