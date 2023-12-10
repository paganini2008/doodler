package io.doodler.common.security;

import static io.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import static io.doodler.common.security.SecurityConstants.FACEBOOK_API_URI;
import static io.doodler.common.security.SecurityConstants.GOOGLE_API_URI;
import static io.doodler.common.security.SecurityConstants.LINE_API_URI;
import static io.doodler.common.security.SecurityConstants.LOGIN_AUTHENTICATOR;
import static io.doodler.common.security.SecurityConstants.MULTIPLE_LOGIN_AUTHENTICATOR;
import static io.doodler.common.security.SecurityConstants.REMEMBER_ME_KEY;
import static io.doodler.common.security.SecurityConstants.TWITCH_API_URI;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import io.doodler.common.BizException;
import io.doodler.common.ErrorCode;
import io.doodler.common.ExceptionTransferer;
import io.doodler.common.context.ConditionalOnApplication;
import io.doodler.common.context.ConditionalOnNotApplication;
import io.doodler.common.feign.RestClientUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.doodler.common.security.facebook.FacebookAuthenticationProvider;
import io.doodler.common.security.facebook.FacebookClientApiService;
import io.doodler.common.security.google.GoogleAuthenticationProvider;
import io.doodler.common.security.google.GoogleClientApiService;
import io.doodler.common.security.line.LineAuthenticationProvider;
import io.doodler.common.security.line.LineClientApiService;
import io.doodler.common.security.totp.TotpAuthenticationProvider;
import io.doodler.common.security.twitch.TwitchAuthenticationProvider;
import io.doodler.common.security.twitch.TwitchClientApiService;
import lombok.SneakyThrows;

/**
 * @Description: WebSecurityConfig
 * @Author: Fred Feng
 * @Date: 19/11/2022
 * @Version 1.0.0
 */
@Order(90)
@EnableConfigurationProperties({
        JwtProperties.class,
        WhiteListProperties.class,
        SecurityClientProperties.class,
        RestClientProperties.class})
@Import({HttpSecurityConfig.class,
        PermissionAccessChecker.class,
        SocialAccountService.class,
        LoginFailureHandlerAware.class,
        JwtAuthenticationFailureHandlerAware.class,
        AuthenticationExceptionAwareHandler.class,
        AuthenticationController.class})
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    @Bean
    public ExceptionTransferer noneHandlerBizExceptionTransferer() {
        return new NoneHandlerBizExceptionTransferer();
    }

    @ConditionalOnMissingBean
    @Bean
    public LoginFailureListener loginFailureListener() {
        return new NoOpLoginFailureListener();
    }
    
    @ConditionalOnMissingBean
    @Bean
    public JwtAuthenticationFailureListener jwtAuthenticationFailureListener(Marker marker) {
        return new LoggingJwtAuthenticationFailureListener(marker);
    }

    @ConditionalOnMissingBean
    @Bean
    public TokenStrategy tokenStrategy(JwtProperties jwtProperties) {
        MixedTokenStrategy tokenStrategy = new MixedTokenStrategy();
        JwtTokenStrategy jwtTokenStrategy = new JwtTokenStrategy(jwtProperties);
        tokenStrategy.addTokenStrategy(AUTHORIZATION_TYPE_BEARER, jwtTokenStrategy);
        tokenStrategy.setDefaultTokenStrategy(jwtTokenStrategy);
        return tokenStrategy;
    }

    @ConditionalOnMissingBean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @ConditionalOnMissingBean
    @Bean
    public PlatformUserDetailsService userDetailsService(RedisOperations<String, Object> redisOperations) {
        return new PlatformUserDetailsServiceImpl(redisOperations);
    }

    @SneakyThrows
    @Autowired
    public void configureBasicUser(PlatformUserDetailsService platformUserDetailsService) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
        List<BasicCredentials> list = null;
        try {
            list = objectMapper.readValue(new ClassPathResource("users.yaml").getInputStream(),
                    new TypeReference<List<BasicCredentials>>() {
                    });
        } catch (IOException ignored) {
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (BasicCredentials credentials : list) {
                platformUserDetailsService.addBasicUser(credentials.getUsername(),
                        credentials.getPassword(),
                        credentials.getPlatform(),
                        credentials.getRoles());
            }
        }
    }

    @Bean
    public AbstractRememberMeServices rememberMeServices(SecurityClientProperties securityClientProperties,
                                                         PlatformUserDetailsService userDetailsService) {
        PlatformTokenBasedRememberMeServices rememberMeServices = new PlatformTokenBasedRememberMeServices(REMEMBER_ME_KEY,
                userDetailsService);
        rememberMeServices.setParameter(REMEMBER_ME_KEY);
        rememberMeServices.setTokenValiditySeconds(securityClientProperties.getRememberMeDuration());
        return rememberMeServices;
    }

    @ConditionalOnNotApplication(applicationNames = {LOGIN_AUTHENTICATOR, MULTIPLE_LOGIN_AUTHENTICATOR})
    @Bean
    @SneakyThrows
    public AuthenticationManager authManager(HttpSecurity http, PlatformUserDetailsService userDetailsService) {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new RememberMeAuthenticationProvider(REMEMBER_ME_KEY));
        authenticationManagerBuilder.authenticationProvider(new BasicAuthenticationProvider(userDetailsService));
        return authenticationManagerBuilder.build();
    }

    @ConditionalOnApplication(applicationNames = LOGIN_AUTHENTICATOR)
    @Configuration(proxyBeanMethods = false)
    public static class LoginAuthenticatorConfig {

        @Bean
        @SneakyThrows
        public AuthenticationManager authManager(HttpSecurity http,
                                                 SuperAdminPassword superAdminPassword,
                                                 PlatformUserDetailsService userDetailsService,
                                                 PasswordEncoder passwordEncoder) {
            AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                    AuthenticationManagerBuilder.class);
            authenticationManagerBuilder.authenticationProvider(new RememberMeAuthenticationProvider(REMEMBER_ME_KEY));
            authenticationManagerBuilder.authenticationProvider(new SuperAdminAuthenticationProvider(superAdminPassword));
            authenticationManagerBuilder.authenticationProvider(
                    new TotpAuthenticationProvider(userDetailsService, passwordEncoder));
            authenticationManagerBuilder.authenticationProvider(new BasicAuthenticationProvider(userDetailsService));
            return authenticationManagerBuilder.build();
        }

        @Bean
        public SuperAdminPassword superAdminPassword(SecurityClientProperties securityClientProperties,
                                                     PasswordEncoder passwordEncoder) {
            return new SuperAdminPassword(securityClientProperties, passwordEncoder);
        }

        @Bean
        public AuthenticationService authenticationService(AuthenticationManager authenticationManager,
                                                           TokenStrategy tokenStrategy,
                                                           RedisOperations<String, Object> redisOperations,
                                                           SecurityClientProperties securityClientProperties,
                                                           AbstractRememberMeServices rememberMeServices,
                                                           PlatformUserDetailsService userDetailsService,
                                                           Marker marker) {
            return new WebAppAuthenticationService(authenticationManager, tokenStrategy, redisOperations,
                    securityClientProperties, rememberMeServices, userDetailsService, marker);
        }
    }

    @ConditionalOnApplication(applicationNames = MULTIPLE_LOGIN_AUTHENTICATOR)
    @Configuration(proxyBeanMethods = false)
    public static class MultipleLoginAuthenticatorConfig {

        @SneakyThrows
        @Bean
        public AuthenticationManager authManager(HttpSecurity http,
                                                 PlatformUserDetailsService userDetailsService,
                                                 PasswordEncoder passwordEncoder,
                                                 GoogleClientApiService googleClientApiService,
                                                 FacebookClientApiService facebookClientApiService,
                                                 TwitchClientApiService twitchClientApiService,
                                                 LineClientApiService lineClientApiService) {
            AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                    AuthenticationManagerBuilder.class);
            authenticationManagerBuilder.authenticationProvider(
                    new GoogleAuthenticationProvider(googleClientApiService, userDetailsService));
            authenticationManagerBuilder.authenticationProvider(
                    new FacebookAuthenticationProvider(facebookClientApiService, userDetailsService));
            authenticationManagerBuilder.authenticationProvider(
                    new TwitchAuthenticationProvider(twitchClientApiService, userDetailsService));
            authenticationManagerBuilder.authenticationProvider(
                    new LineAuthenticationProvider(lineClientApiService, userDetailsService));
            authenticationManagerBuilder.authenticationProvider(
                    new TotpAuthenticationProvider(userDetailsService, passwordEncoder));
            authenticationManagerBuilder.authenticationProvider(new BasicAuthenticationProvider(userDetailsService));
            authenticationManagerBuilder.authenticationProvider(new RememberMeAuthenticationProvider(REMEMBER_ME_KEY));
            return authenticationManagerBuilder.build();
        }

        @Bean
        public AuthenticationService authenticationService(AuthenticationManager authenticationManager,
                                                           TokenStrategy tokenStrategy,
                                                           RedisOperations<String, Object> redisOperations,
                                                           SecurityClientProperties securityClientProperties,
                                                           AbstractRememberMeServices rememberMeServices,
                                                           PlatformUserDetailsService userDetailsService,
                                                           Marker marker) {
            return new WebAppAuthenticationService(authenticationManager, tokenStrategy, redisOperations,
                    securityClientProperties, rememberMeServices, userDetailsService, marker);
        }

        @ConditionalOnMissingBean
        @Bean
        public GoogleClientApiService googleClientApiService() {
            return RestClientUtils.openRestClient(GoogleClientApiService.class, GOOGLE_API_URI, 3);
        }

        @ConditionalOnMissingBean
        @Bean
        public FacebookClientApiService facebookClientApiService() {
            return RestClientUtils.openRestClient(FacebookClientApiService.class, FACEBOOK_API_URI, 3);
        }

        @ConditionalOnMissingBean
        @Bean
        public TwitchClientApiService twitchClientApiService() {
            return RestClientUtils.openRestClient(TwitchClientApiService.class, TWITCH_API_URI, 3);
        }

        @ConditionalOnMissingBean
        @Bean
        public LineClientApiService lineClientApiService() {
            return RestClientUtils.openRestClient(LineClientApiService.class, LINE_API_URI, 3);
        }
    }

    /**
     * @Description: NoneHandlerBizExceptionTransferer
     * @Author: Fred Feng
     * @Date: 18/01/2023
     * @Version 1.0.0
     */
    private static class NoneHandlerBizExceptionTransferer implements ExceptionTransferer {

        @Override
        public Throwable transfer(Throwable e) {
            if (e instanceof AuthenticationException) {
                ErrorCode errorCode = ErrorCodes.matches((AuthenticationException) e);
                return new BizException(errorCode, HttpStatus.UNAUTHORIZED);
            }
            return e;
        }
    }

    @Bean
    public PostAuthorizationAdvice permissionPostAuthorizationHandler() {
        return new PermissionPostAuthorizationAdvice();
    }

    @ConditionalOnMissingBean
    @Bean
    public HttpSecurityCustomizer noopHttpSecurityCustomizer() {
        return new NoOpHttpSecurityCustomizer();
    }

    @ConditionalOnClass(name = {"springfox.documentation.spi.service.OperationBuilderPlugin"})
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1000)
    public WhiteListBuilderPlugin whiteListBuilderPlugin() {
        return new WhiteListBuilderPlugin();
    }
    
    @Bean
    public PerRequestSecurityContextExchanger perRequestSecurityContextExchanger() {
    	return new PerRequestSecurityContextExchanger();
    }
}