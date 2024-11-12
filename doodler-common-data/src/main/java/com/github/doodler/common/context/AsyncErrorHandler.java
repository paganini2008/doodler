package com.github.doodler.common.context;

import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: AsyncErrorHandler
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class AsyncErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }
}