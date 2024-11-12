package com.github.doodler.common.security.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.security.AuthenticationService;
import com.github.doodler.common.security.ConditionalOnSecureLogin;
import com.github.doodler.common.security.HttpSecurityCustomizer;
import com.github.doodler.common.security.PlatformUserDetailsService;

/**
 * 
 * @Description: OAuth2Config
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
@ConditionalOnSecureLogin("oauth2ClientEnabled")
@Configuration(proxyBeanMethods = false)
public class OAuth2ClientConfig {

    @ConditionalOnMissingBean
    @Bean
    public OAuth2UserInfoService oAuth2UserDetailsService() {
        return new NoOpOAuth2UserInfoService();
    }

    @Bean
    public WebAppOAuth2UserService oAuth2UserService(PlatformUserDetailsService userDetailsService,
            OAuth2UserInfoService oAuth2UserInfoService) {
        return new WebAppOAuth2UserService(userDetailsService, oAuth2UserInfoService);
    }

    @Bean
    public WebAppOidcUserService oidcUserService(PlatformUserDetailsService userDetailsService,
            OAuth2UserInfoService oAuth2UserInfoService) {
        return new WebAppOidcUserService(userDetailsService, oAuth2UserInfoService);
    }

    @Bean
    public OAuth2AuthenticationPostHandler oAuth2AuthenticationPostHandler(
            OAuth2ClientPostHandler postHandler) {
        return new OAuth2AuthenticationPostHandler(postHandler);
    }

    @Bean
    public HttpSecurityCustomizer httpSecurityCustomizer(WebAppOidcUserService oidcUserService,
            WebAppOAuth2UserService oAuth2UserService,
            OAuth2AuthenticationPostHandler postHandler) {
        return new OAuth2ConfigCustomizer(oidcUserService, oAuth2UserService, postHandler);
    }

    @ConditionalOnMissingBean
    @Bean
    public OAuth2ClientPostHandler oAuth2ClientPostHandler(
            @Lazy AuthenticationService authenticationService, ObjectMapper objectMapper) {
        return new TokenOAuth2ClientPostHandler(authenticationService, objectMapper);
    }

}
