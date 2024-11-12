package com.github.doodler.common.security.oauth2;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.github.doodler.common.security.HttpSecurityCustomizer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 
 * @Description: OAuth2ConfigCustomizer
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OAuth2ConfigCustomizer implements HttpSecurityCustomizer {

    private final WebAppOidcUserService oidcUserService;
    private final WebAppOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationPostHandler authenticationPostHandler;

    @SneakyThrows
    @Override
    public void customize(HttpSecurity http) {
        http.oauth2Login().successHandler(authenticationPostHandler)
                .failureHandler(authenticationPostHandler).userInfoEndpoint()
                .oidcUserService(oidcUserService).userService(oAuth2UserService).and()
                .loginPage("/website/my-socal-login");
        // .defaultSuccessUrl("/website/welcome", true);
    }

}
