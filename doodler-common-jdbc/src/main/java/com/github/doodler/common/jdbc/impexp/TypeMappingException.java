package com.github.doodler.common.jdbc.impexp;

/**
 * @Description: TypeMappingException
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class TypeMappingException extends ImpExpException {

    private static final long serialVersionUID = 7412432081347389457L;

    public TypeMappingException(String msg) {
        super(msg);
    }

    public TypeMappingException(String msg, Throwable e) {
        super(msg, e);
    }

    public TypeMappingException(Throwable e) {
        super(e);
    }
}