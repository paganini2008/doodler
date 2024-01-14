package com.github.doodler.common.security;

/**
 * 
 * @Description: JwtAuthorizationFailureListener
 * @Author: Fred Feng
 * @Date: 28/11/2023
 * @Version 1.0.0
 */
public interface JwtAuthenticationFailureListener {

    default void onAuthenticationFailed(JwtAuthenticationException e) {
    }
	
}
