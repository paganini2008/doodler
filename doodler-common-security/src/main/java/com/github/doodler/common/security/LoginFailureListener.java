package com.github.doodler.common.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: LoginFailureListener
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
public interface LoginFailureListener {

    default void onTryAgain(AuthenticationException e) {
    }

    default void onDisabled(AuthenticationException e) {
    }

    default void onLocked(AuthenticationException e) {
    }
}