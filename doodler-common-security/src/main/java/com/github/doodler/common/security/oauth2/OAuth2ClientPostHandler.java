package com.github.doodler.common.security.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @Description: OAuth2ClientPostHandler
 * @Author: Fred Feng
 * @Date: 20/10/2024
 * @Version 1.0.0
 */
public interface OAuth2ClientPostHandler {

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws Exception;

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException e) throws Exception;

}
