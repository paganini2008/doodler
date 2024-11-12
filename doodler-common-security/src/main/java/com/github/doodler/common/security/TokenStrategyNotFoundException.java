package com.github.doodler.common.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: TokenStrategyNotFoundException
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
public class TokenStrategyNotFoundException extends AuthenticationException {

    private static final long serialVersionUID = 8912813224921608634L;
    
    public TokenStrategyNotFoundException() {
    	super("");
    }

    public TokenStrategyNotFoundException(String msg) {
        super(msg);
    }
}