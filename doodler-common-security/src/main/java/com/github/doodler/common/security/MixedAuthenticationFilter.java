package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BASIC;
import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import static com.github.doodler.common.security.SecurityConstants.LOGIN_KEY;
import static com.github.doodler.common.security.SecurityConstants.REMEMBER_ME_KEY;
import static com.github.doodler.common.security.SecurityConstants.TOKEN_KEY;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.github.doodler.common.BizException;
import com.github.doodler.common.utils.WebUtils;

/**
 * @Description: MixedAuthenticationFilter
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class MixedAuthenticationFilter extends BasicAuthenticationFilter {

    public MixedAuthenticationFilter(AuthenticationManager authenticationManager,
            SecurityClientProperties securityClientProperties, List<RequestMatcher> requestMatchers,
            TokenStrategy tokenStrategy, RedisOperations<String, Object> redisOperations,
            HandlerExceptionResolver handlerExceptionResolver) {
        super(authenticationManager);
        this.securityClientProperties = securityClientProperties;
        this.requestMatchers = requestMatchers;
        this.tokenStrategy = tokenStrategy;
        this.redisOperations = redisOperations;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private final SecurityClientProperties securityClientProperties;
    private final List<RequestMatcher> requestMatchers;
    private final TokenStrategy tokenStrategy;
    private final RedisOperations<String, Object> redisOperations;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    private boolean isMatchedRequestPath(HttpServletRequest request) {
        boolean result = requestMatchers.stream().anyMatch(m -> m.matches(request));
        return result;
    }

    private boolean isAuthorizationTypeSupported(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            return false;
        }
        if (authorization.startsWith(AUTHORIZATION_TYPE_BEARER)) {
            return true;
        }
        boolean result = authorization.startsWith(AUTHORIZATION_TYPE_BASIC)
                && securityClientProperties.isBasicEnabled();
        return result;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        if (StringUtils.isNotBlank(WebUtils.getCookieValue(REMEMBER_ME_KEY))
                || isMatchedRequestPath(request) || !isAuthorizationTypeSupported(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizationHeaderValue)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = null;
        try {
            String[] args = resolveToken(authorizationHeaderValue);
            String authType = args[0];
            if (AUTHORIZATION_TYPE_BASIC.equals(authType)) {
                super.doFilterInternal(request, response, filterChain);
                return;
            }
            token = args[1];
            IdentifiableUserDetails user = tokenStrategy.decode(authorizationHeaderValue);
            if (tokenStrategy.validate(authorizationHeaderValue)) {
                String key = String.format(TOKEN_KEY, user.getPlatform(), token);
                AuthenticationInfo authInfo =
                        (AuthenticationInfo) redisOperations.opsForValue().get(key);
                if (authInfo == null) {
                    throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION,
                            HttpStatus.UNAUTHORIZED);
                }
                if (!authInfo.getId().equals(user.getId())
                        || !authInfo.getUsername().equals(user.getUsername())
                        || !authInfo.getEmail().equals(user.getEmail())
                        || !authInfo.getPlatform().equals(user.getPlatform())) {
                    throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION,
                            HttpStatus.UNAUTHORIZED);
                }
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (authInfo.hasSa()) {
                        user = SuperAdmin.INSTANCE;
                    } else {
                        user = new RegularUser(authInfo.getId(), user.getUsername(),
                                user.getPassword(), authInfo.getEmail(), user.getPlatform(),
                                user.isEnabled(), SecurityUtils
                                        .getGrantedAuthorities(authInfo.getGrantedAuthorities()));
                        if (MapUtils.isNotEmpty(authInfo.getAdditionalInformation())) {
                            user.getAdditionalInformation()
                                    .putAll(authInfo.getAdditionalInformation());
                        }
                    }
                    userDetailsChecker.check(user);
                    InternalAuthenticationToken authentication =
                            new InternalAuthenticationToken(user, user.getUsername(),
                                    user.getPlatform(), false, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    onSuccessfulAuthentication(request, response, authentication);
                }
            } else {
                String key = String.format(LOGIN_KEY, user.getPlatform(), user.getIdentity());
                redisOperations.delete(key);

                key = String.format(TOKEN_KEY, user.getPlatform(), token);
                redisOperations.delete(key);

                throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION, HttpStatus.UNAUTHORIZED);
            }
        } catch (RuntimeException e) {
            AuthenticationException authenticationException =
                    (e instanceof AuthenticationException) ? (AuthenticationException) e
                            : new JwtAuthenticationException(token, e.getMessage(), e);
            onUnsuccessfulAuthentication(request, response, authenticationException);
            handlerExceptionResolver.resolveException(request, response, null,
                    authenticationException);
            throw authenticationException;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException failed) throws IOException {
        super.onUnsuccessfulAuthentication(request, response, failed);
    }

    private String[] resolveToken(String authorization) {
        String[] args = null;
        try {
            if (authorization.startsWith(AUTHORIZATION_TYPE_BEARER)) {
                args = new String[] {AUTHORIZATION_TYPE_BEARER, authorization.substring(7)};
            }
            if (authorization.startsWith(AUTHORIZATION_TYPE_BASIC)) {
                args = new String[] {AUTHORIZATION_TYPE_BASIC, authorization.substring(6)};
            }
        } catch (RuntimeException ignored) {
        }
        if (args == null) {
            throw new BizException(ErrorCodes.JWT_TOKEN_BAD_FORMAT, HttpStatus.UNAUTHORIZED,
                    authorization);
        }
        return args;
    }
}
