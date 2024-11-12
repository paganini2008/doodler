package com.github.doodler.common.oauth2;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 
 * @Description: OAuth2AuthenticationPostHandler
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
public interface OAuth2AuthenticationPostHandler
        extends AuthenticationSuccessHandler, AuthenticationFailureHandler {

}
