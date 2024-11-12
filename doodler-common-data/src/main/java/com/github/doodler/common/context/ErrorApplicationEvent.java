package com.github.doodler.common.context;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: ErrorApplicationEvent
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
public class ErrorApplicationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4282358234515434439L;

    public ErrorApplicationEvent(Object source, Throwable cause) {
        super(source);
        this.cause = cause;
    }

    private final Throwable cause;

    public Throwable getCause() {
        return cause;
    }
}