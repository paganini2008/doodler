package com.github.doodler.common.security;

/**
 * @Description: PostAuthorizationAdvice
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
public interface PostAuthorizationAdvice {

    default void postAuthorizeRoles(boolean approved, String role) {
    }

    default void postAuthorizePermissions(boolean approved, String permission) {
    }
}