package io.doodler.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: LoginFailureExceptionListener
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
public interface LoginFailureExceptionListener {

    default void onTryAgain(AuthenticationException e) {
    }

    default void onDisabled(AuthenticationException e) {
    }

    default void onLocked(AuthenticationException e) {
    }
}