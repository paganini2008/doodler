package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

/**
 * @Description: PlatformRememberMeAuthenticationFilter
 * @Author: Fred Feng
 * @Date: 22/12/2022
 * @Version 1.0.0
 */
@Slf4j
public class PlatformRememberMeAuthenticationFilter extends RememberMeAuthenticationFilter {

    public PlatformRememberMeAuthenticationFilter(AuthenticationManager authenticationManager,
                                                  RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              Authentication authResult) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication with remember-me: {}", authResult.toString());
        }
        if (authResult instanceof RememberMeAuthenticationToken) {
            RegularUser user = (RegularUser) authResult.getPrincipal();
            InternalAuthenticationToken authentication = new InternalAuthenticationToken(user,
                    user.getUsername(),
                    user.getPlatform(),
                    true,
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException e) {
        super.onUnsuccessfulAuthentication(request, response, e);
        if (log.isErrorEnabled()) {
            log.error("[RequestURI: {}]: {}", request.getRequestURI(), e.getMessage(), e);
        }
    }
}