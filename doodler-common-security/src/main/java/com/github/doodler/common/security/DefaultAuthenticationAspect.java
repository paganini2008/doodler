package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Description: DefaultAuthenticationAspect
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
@Slf4j
public class DefaultAuthenticationAspect implements AuthenticationAspect {

    @Override
    public void onLogin(AbstractAuthenticationToken token, HttpServletRequest request, HttpServletResponse response) {
        if (log.isTraceEnabled()) {
            log.trace("[On Login]: {}", token);
        }
    }

    @Override
    public void onSuccess(AbstractAuthenticationToken token, UserDetails user, HttpServletRequest request,
                          HttpServletResponse response) {
        if (log.isTraceEnabled()) {
            log.trace("[On Success]: {}", token);
        }
    }

    @Override
    public void onFailure(AbstractAuthenticationToken token, AuthenticationException e, HttpServletRequest request,
                          HttpServletResponse response) {
        if (log.isTraceEnabled()) {
            log.trace("[On Failure]: {}", token, e);
        }
    }

    @Override
    public void onLogout(UserDetails user, LogoutReason logoutReason, HttpServletRequest request,
                         HttpServletResponse response) {
        if (log.isTraceEnabled()) {
            log.trace("[On Logout]: {}", user);
        }
    }
}