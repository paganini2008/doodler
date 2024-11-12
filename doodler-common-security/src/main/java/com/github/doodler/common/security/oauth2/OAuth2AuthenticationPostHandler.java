package com.github.doodler.common.security.oauth2;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: OAuth2AuthenticationPostHandler
 * @Author: Fred Feng
 * @Date: 16/10/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationPostHandler
        implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final OAuth2ClientPostHandler oauth2ClientPostHandler;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken =
                (OAuth2AuthenticationToken) authentication;
        OAuth2UserInfoAware oAuth2UserInfoAware =
                (OAuth2UserInfoAware) oAuth2AuthenticationToken.getPrincipal();
        if (log.isInfoEnabled()) {
            log.info(oAuth2UserInfoAware.getOAuth2UserInfo().toString());
        }
        oauth2ClientPostHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        oauth2ClientPostHandler.onAuthenticationFailure(request, response, e);
    }

}
