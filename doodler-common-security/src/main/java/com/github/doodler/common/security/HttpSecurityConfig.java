package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.ROLE_SUPPORTER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.context.ContextPath;
import com.github.doodler.common.context.MessageLocalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HttpSecurityConfig
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class HttpSecurityConfig {

    static final String[] DEFAULT_PERMITTED_URLS = new String[] {"/", "/error", "/ping", "/login",
            "/login/**", "/register", "/social-login", "/welcome", "/favicon.ico", "/oauth2/**",
            "/.well-known/**", "/my-social-login"};

    static final String[] STATIC_RESOURCES =
            new String[] {"/", "/static/**", "/swagger-ui.html", "/webjars/**", "/v2/**",
                    "/swagger-resources/**", "/doc.html", "/favicon.ico", "/csrf", "/druid/**"};


    private final ContextPath contextPath;
    private final ObjectMapper objectMapper;
    private final MessageLocalization messageLocalization;
    private final TokenStrategy tokenStrategy;
    private final WhiteListProperties whiteListProperties;
    private final RestClientProperties restClientProperties;
    private final SecurityClientProperties securityClientProperties;
    private final RedisOperations<String, Object> redisOperations;
    private final List<HttpSecurityCustomizer> httpCustomizers;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Value("${server.port}")
    private int port;

    @Value("${management.server.port:0}")
    private int actuatorPort;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationManager authenticationManager,
            AbstractRememberMeServices rememberMeServices) throws Exception {
        http.csrf().disable().httpBasic().disable().formLogin().disable().logout().disable().cors()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(getPermittedUrls()).permitAll().mvcMatchers(getPermittedUrls())
                .servletPath(contextPath.getContextPath()).permitAll()
                .requestMatchers(
                        new WhiteListRequestMatcher(whiteListProperties, restClientProperties))
                .permitAll().requestMatchers(new ActuatorHandlerRequestMatcher(port, actuatorPort))
                .permitAll().antMatchers("/monitor/prometheus").permitAll()
                .antMatchers("/monitor/**").hasAuthority(ROLE_SUPPORTER).mvcMatchers("/actuator/**")
                .servletPath(contextPath.getContextPath()).hasAuthority(ROLE_SUPPORTER)
                // .antMatchers("/monitor/**")
                // .permitAll()
                // .mvcMatchers("/actuator/**")
                // .servletPath(servletContextPath)
                // .permitAll()
                .anyRequest().authenticated().and().rememberMe()
                .rememberMeServices(rememberMeServices).and().exceptionHandling()
                .authenticationEntryPoint(
                        new ForbiddenAuthenticationEntryPoint(objectMapper, messageLocalization))
                .accessDeniedHandler(
                        new GlobalAccessDeniedHandler(objectMapper, messageLocalization));
        http.addFilterBefore(new WebCorsFilter(), ChannelProcessingFilter.class)
                .addFilter(new MixedAuthenticationFilter(authenticationManager,
                        securityClientProperties, Collections.emptyList(), tokenStrategy,
                        redisOperations, handlerExceptionResolver))
                .addFilterAt(platformRememberMeAuthenticationFilter(authenticationManager,
                        rememberMeServices), RememberMeAuthenticationFilter.class);
        httpCustomizers.forEach(c -> c.customize(http));
        return http.build();
    }

    private String[] getPermittedUrls() {
        List<String> urls = new ArrayList<>(Arrays.asList(DEFAULT_PERMITTED_URLS));
        if (CollectionUtils.isNotEmpty(securityClientProperties.getPermittedUrls())) {
            urls.addAll(securityClientProperties.getPermittedUrls());
        }
        if (log.isTraceEnabled()) {
            log.trace("Permitted urls: {}", urls.toString());
        }
        return urls.toArray(new String[0]);
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall httpFirewall = new StrictHttpFirewall();
        httpFirewall.setAllowSemicolon(true);
        httpFirewall.setAllowUrlEncodedPeriod(true);
        httpFirewall.setAllowUrlEncodedSlash(true);
        httpFirewall.setAllowUrlEncodedPercent(true);
        httpFirewall.setAllowUrlEncodedDoubleSlash(true);
        httpFirewall.setAllowedHeaderValues(t -> true);
        return httpFirewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(httpFirewall()).ignoring().antMatchers(STATIC_RESOURCES)
                .mvcMatchers(STATIC_RESOURCES).servletPath(contextPath.getContextPath());
    }

    @Bean
    public PlatformRememberMeAuthenticationFilter platformRememberMeAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AbstractRememberMeServices rememberMeServices) {
        return new PlatformRememberMeAuthenticationFilter(authenticationManager,
                rememberMeServices);
    }
}
