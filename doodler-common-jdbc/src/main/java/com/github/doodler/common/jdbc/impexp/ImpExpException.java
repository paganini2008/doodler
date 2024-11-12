package com.github.doodler.common.jdbc.impexp;

/**
 * @Description: ImpexpException
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class ImpExpException extends RuntimeException {

    private static final long serialVersionUID = 4468454097436850680L;

    public ImpExpException(String msg) {
        super(msg);
    }

    public ImpExpException(String msg, Throwable e) {
        super(msg, e);
    }

    public ImpExpException(Throwable e) {
        super(e);
    }
}