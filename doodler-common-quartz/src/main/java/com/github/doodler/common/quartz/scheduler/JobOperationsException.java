package com.github.doodler.common.quartz.scheduler;

import org.springframework.web.client.RestClientException;

/**
 * @Description: JobOperationsException
 * @Author: Fred Feng
 * @Date: 19/06/2023
 * @Version 1.0.0
 */
public class JobOperationsException extends RestClientException {

    private static final long serialVersionUID = 7183129251270391237L;

    public JobOperationsException(String msg) {
        super(msg);
    }

    public JobOperationsException(String msg, Throwable e) {
        super(msg, e);
    }
}