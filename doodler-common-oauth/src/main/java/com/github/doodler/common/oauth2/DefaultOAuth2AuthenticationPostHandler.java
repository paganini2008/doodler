package com.github.doodler.common.oauth2;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: DefaultOAuth2AuthenticationPostHandler
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
@Slf4j
public class DefaultOAuth2AuthenticationPostHandler implements OAuth2AuthenticationPostHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (log.isInfoEnabled()) {
            log.info(authentication.toString());
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }

}
