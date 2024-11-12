package com.github.doodler.common.ws;

/**
 * @Description: ErrorHandler
 * @Author: Fred Feng
 * @Date: 11/01/2023
 * @Version 1.0.0
 */
public interface ErrorHandler {

    void handleResult(boolean ok, Throwable e);
}