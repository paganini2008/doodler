package com.github.doodler.common.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: JwtAuthenticationException
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class JwtAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -7328093761114226130L;

    private final String token;

    public JwtAuthenticationException(String token, String message) {
        super(message);
        this.token = token;
    }

    public JwtAuthenticationException(String token, String message, Throwable cause) {
        super(message, cause);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}