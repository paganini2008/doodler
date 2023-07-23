package io.doodler.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: JwtAuthenticationException
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class JwtAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -7328093761114226130L;

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}