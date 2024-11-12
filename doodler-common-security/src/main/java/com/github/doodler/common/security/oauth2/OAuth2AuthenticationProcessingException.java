package com.github.doodler.common.security.oauth2;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @Description: OAuth2AuthenticationProcessingException
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {

    private static final long serialVersionUID = -374750117330843232L;

    public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
