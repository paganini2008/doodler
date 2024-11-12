package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import static com.github.doodler.common.security.SecurityConstants.REMEMBER_ME_KEY;
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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.doodler.common.BizException;
import com.github.doodler.common.DefaultExceptionTransformer;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionTransformer;
import com.github.doodler.common.security.oauth2.OAuth2ClientAuthenticationProvider;
import com.github.doodler.common.security.oauth2.OAuth2ClientConfig;
import com.github.doodler.common.security.oauth2.OAuth2UserInfoService;
import com.github.doodler.common.security.oauth2.SimpleOAuth2ClientAuthenticationProvider;
import com.github.doodler.common.security.otp.OtpAuthenticationProvider;
import lombok.SneakyThrows;
import springfox.documentation.spi.service.OperationBuilderPlugin;

/**
 * @Description: WebSecurityConfig
 * @Author: Fred Feng
 * @Date: 19/11/2022
 * @Version 1.0.0
 */
@Order(90)
@EnableConfigurationProperties({JwtProperties.class, WhiteListProperties.class,
        SecurityClientProperties.class, RestClientProperties.class})
@Import({HttpSecurityConfig.class, OAuth2ClientConfig.class, PermissionAccessChecker.class,
        LoginFailureHandlerAware.class, JwtAuthenticationFailureHandlerAware.class,
        AuthenticationExceptionAwareHandler.class, AuthenticationController.class})
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    @ConditionalOnMissingBean
    @Bean
    public ExceptionTransformer noneHandlerExceptionTransformer() {
        return new NoneHandlerExceptionTransformer();
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
    public PlatformUserDetailsService userDetailsService(
            RedisOperations<String, Object> redisOperations) {
        return new PlatformUserDetailsServiceImpl(redisOperations);
    }

    @SneakyThrows
    @Autowired
    public void configureBasicUser(UserDetailsManager userDetailsManager) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
        List<BasicCredentials> list = null;
        try {
            list = objectMapper.readValue(new ClassPathResource("users.yaml").getInputStream(),
                    new TypeReference<List<BasicCredentials>>() {});
        } catch (IOException ignored) {
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (BasicCredentials credentials : list) {
                userDetailsManager.createUser(credentials);
            }
        }
    }

    @Bean
    public AbstractRememberMeServices rememberMeServices(
            SecurityClientProperties securityClientProperties,
            PlatformUserDetailsService userDetailsService) {
        PlatformTokenBasedRememberMeServices rememberMeServices =
                new PlatformTokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService);
        rememberMeServices.setParameter(REMEMBER_ME_KEY);
        rememberMeServices
                .setTokenValiditySeconds(securityClientProperties.getRememberMeDuration());
        return rememberMeServices;
    }

    /**
     * 
     * @Description: AuthenticationProviderConfig
     * @Author: Fred Feng
     * @Date: 28/10/2024
     * @Version 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    public static class AuthenticationProviderConfig {

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider(
                PlatformUserDetailsService platformUserDetailsService,
                PasswordEncoder passwordEncoder) {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
            daoAuthenticationProvider.setUserDetailsService(platformUserDetailsService);
            return daoAuthenticationProvider;
        }

        @Bean
        public BasicUserAuthenticationProvider basicUserAuthenticationProvider(
                PlatformUserDetailsService platformUserDetailsService) {
            return new BasicUserAuthenticationProvider(platformUserDetailsService);
        }

        @Bean
        public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
            return new RememberMeAuthenticationProvider(REMEMBER_ME_KEY);
        }

        @Bean
        public OtpAuthenticationProvider otpAuthenticationProvider(
                PlatformUserDetailsService platformUserDetailsService,
                PasswordEncoder passwordEncoder) {
            return new OtpAuthenticationProvider(platformUserDetailsService, passwordEncoder);
        }

        @ConditionalOnSecureLogin("oauth2ClientEnabled")
        @Bean
        public OAuth2ClientAuthenticationProvider auth2ClientAuthenticationProvider(
                PlatformUserDetailsService platformUserDetailsService) {
            return new OAuth2ClientAuthenticationProvider(platformUserDetailsService);
        }

        @ConditionalOnSecureLogin("oauth2ClientEnabled")
        @Bean
        public SimpleOAuth2ClientAuthenticationProvider simpleOAuth2ClientAuthenticationProvider(
                ClientRegistrationRepository clientRegistrationRepository,
                DefaultOAuth2UserService oAuth2UserService,
                PlatformUserDetailsService userDetailsService,
                OAuth2UserInfoService oauth2UserInfoService) {
            return new SimpleOAuth2ClientAuthenticationProvider(clientRegistrationRepository,
                    oAuth2UserService, userDetailsService, oauth2UserInfoService);
        }

        @ConditionalOnSecureLogin("superAdminEnabled")
        @Bean
        public SuperAdminAuthenticationProvider superAdminAuthenticationProvider(
                SuperAdminPassword superAdminPassword) {
            return new SuperAdminAuthenticationProvider(superAdminPassword);
        }

        @ConditionalOnSecureLogin("superAdminEnabled")
        @Bean
        public SuperAdminPassword superAdminPassword(
                SecurityClientProperties securityClientProperties,
                PasswordEncoder passwordEncoder) {
            return new SuperAdminPassword(securityClientProperties, passwordEncoder);
        }
    }

    @SneakyThrows
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
            List<AuthenticationProvider> authenticationProviders) {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
            authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        }
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationService authenticationService(AuthenticationManager authenticationManager,
            TokenStrategy tokenStrategy, RedisOperations<String, Object> redisOperations,
            SecurityClientProperties securityClientProperties,
            AbstractRememberMeServices rememberMeServices,
            PlatformUserDetailsService userDetailsService, Marker marker) {
        return new WebAppAuthenticationService(authenticationManager, tokenStrategy,
                redisOperations, securityClientProperties, rememberMeServices, userDetailsService,
                marker);
    }



    /**
     * @Description: NoneHandlerExceptionTransferer
     * @Author: Fred Feng
     * @Date: 18/01/2023
     * @Version 1.0.0
     */
    private static class NoneHandlerExceptionTransformer extends DefaultExceptionTransformer {

        @Override
        public Throwable transform(Throwable e) {
            if (e instanceof AuthenticationException) {
                ErrorCode errorCode = ErrorCodes.matches((AuthenticationException) e);
                return new BizException(errorCode, HttpStatus.UNAUTHORIZED);
            }
            return super.transform(e);
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

    @ConditionalOnClass(OperationBuilderPlugin.class)
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
