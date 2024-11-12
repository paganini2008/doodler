package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Description: AuthenticationAspect
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
public interface AuthenticationAspect {

    default void onLogin(AbstractAuthenticationToken token, HttpServletRequest request, HttpServletResponse response) {
    }

    default void onSuccess(AbstractAuthenticationToken token, UserDetails user, HttpServletRequest request,
                           HttpServletResponse response) {
    }

    default void onFailure(AbstractAuthenticationToken token, AuthenticationException e, HttpServletRequest request,
                           HttpServletResponse response) {
    }

    default void onLogout(UserDetails user, LogoutReason logoutReason, @Nullable HttpServletRequest request,
                          @Nullable HttpServletResponse response) {
    }
}