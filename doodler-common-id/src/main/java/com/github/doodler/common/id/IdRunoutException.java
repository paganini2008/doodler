package com.github.doodler.common.id;

/**
 * 
 * @Description: IdRunoutException
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public class IdRunoutException extends RuntimeException {

    private static final long serialVersionUID = -4083193883579342106L;

    public IdRunoutException() {
        super();
    }

    public IdRunoutException(String msg) {
        super(msg);
    }

}
