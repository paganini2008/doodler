package com.github.doodler.common;

import org.springframework.http.HttpStatus;

/**
 * @Description: ExceptionDescriptor
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public interface ExceptionDescriptor {

    static final String DEFAULT_MESSAGE_FORMAT = "[ERROR-%s] %s: %s";

    String getMessage();

    ErrorCode getErrorCode();

    default Object getArg() {
        return null;
    }

    default HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}