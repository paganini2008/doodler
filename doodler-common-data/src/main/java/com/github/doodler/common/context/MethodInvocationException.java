package com.github.doodler.common.context;

import org.springframework.beans.BeansException;

/**
 * 
 * @Description: MethodInvocationException
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public class MethodInvocationException extends BeansException {

    private static final long serialVersionUID = 1762091445417308426L;

    public MethodInvocationException(String msg) {
        super(msg);
    }

    public MethodInvocationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
