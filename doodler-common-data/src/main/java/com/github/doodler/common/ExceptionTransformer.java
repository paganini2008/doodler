package com.github.doodler.common;

/**
 * @Description: ExceptionTransformer
 * @Author: Fred Feng
 * @Date: 18/01/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ExceptionTransformer {

    /**
     * Transfer original exception to business exception that developer designed ahead
     * 
     * @param e
     * @return
     */
    Throwable transform(Throwable e);
}
