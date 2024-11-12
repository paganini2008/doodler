package com.github.doodler.common.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.context.MessageLocalization;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: ResourceServerConfig
 * @Author: Fred Feng
 * @Date: 11/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ResourceServerConfig {

    private final ObjectMapper objectMapper;
    private final MessageLocalization messageLocalization;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**",
                "/favicon.ico", "/login.html");
    }

    @Bean
    @Order(101)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().formLogin().disable().csrf().disable()
                .authorizeRequests(authorizeRequests -> authorizeRequests.antMatchers("/test/**")
                        .permitAll().anyRequest().authenticated())
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(new ForbiddenAuthenticationEntryPoint(
                                objectMapper, messageLocalization))
                        .accessDeniedHandler(
                                new GlobalAccessDeniedHandler(objectMapper, messageLocalization)))
                .oauth2ResourceServer((resourceServer) -> resourceServer.jwt());
        return http.build();
    }

}
