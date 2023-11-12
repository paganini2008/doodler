package io.doodler.common.security;

import static io.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BASIC;
import static io.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import static io.doodler.common.security.SecurityConstants.LOGIN_KEY;
import static io.doodler.common.security.SecurityConstants.REMEMBER_ME_KEY;
import static io.doodler.common.security.SecurityConstants.TOKEN_KEY;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.doodler.common.BizException;
import io.doodler.common.utils.WebUtils;

/**
 * @Description: MixedAuthenticationFilter
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class MixedAuthenticationFilter extends BasicAuthenticationFilter {

    public MixedAuthenticationFilter(AuthenticationManager authenticationManager,
                                     SecurityClientProperties securityClientProperties,
                                     List<RequestMatcher> requestMatchers,
                                     TokenStrategy tokenStrategy,
                                     RedisOperations<String, Object> redisOperations,
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
        return requestMatchers.stream().anyMatch(m -> m.matches(request));
    }

    private boolean isAuthorizationTypeSupported(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            return false;
        }
        if (authorization.startsWith(AUTHORIZATION_TYPE_BEARER)) {
            return true;
        }
        return authorization.startsWith(AUTHORIZATION_TYPE_BASIC) && securityClientProperties.isBasicEnabled();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (StringUtils.isNotBlank(WebUtils.getCookieValue(REMEMBER_ME_KEY)) || isMatchedRequestPath(request) ||
                !isAuthorizationTypeSupported(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            try {
                final String token = resolveToken(authorization);
                IdentifiableUserDetails user = tokenStrategy.decode(authorization);
                if (tokenStrategy.validate(authorization)) {
                    String key = String.format(TOKEN_KEY, user.getPlatform(), token);
                    AuthenticationInfo authInfo = (AuthenticationInfo) redisOperations.opsForValue().get(key);
                    if (authInfo == null) {
                        throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION, HttpStatus.UNAUTHORIZED);
                    }
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        if (authInfo.hasSa()) {
                            user = SuperAdmin.INSTANCE;
                        } else {
                            user = new RegularUser(authInfo.getId(), user.getUsername(), user.getPassword(),
                                    user.getPlatform(),
                                    user.isEnabled(),
                                    authInfo.isFirstLogin(),
                                    SecurityUtils.getGrantedAuthorities(authInfo.getGrantedAuthorities()));
                            if (MapUtils.isNotEmpty(authInfo.getAttributes())) {
                                user.getAttributes().putAll(authInfo.getAttributes());
                            }
                        }
                        userDetailsChecker.check(user);
                        InternalAuthenticationToken authentication = new InternalAuthenticationToken(
                                user,
                                user.getUsername(),
                                user.getPlatform(),
                                false,
                                user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    String key = String.format(LOGIN_KEY, user.getPlatform(), user.getUsername());
                    redisOperations.delete(key);

                    key = String.format(TOKEN_KEY, user.getPlatform(), token);
                    redisOperations.delete(key);

                    throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION, HttpStatus.UNAUTHORIZED);
                }
            } catch (RuntimeException e) {
                if (e instanceof BizException) {
                    handlerExceptionResolver.resolveException(request, response, null, e);
                }
                throw e;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(String authorization) {
        try {
            if (authorization.startsWith(AUTHORIZATION_TYPE_BEARER)) {
                return authorization.substring(7);
            }
            if (authorization.startsWith(AUTHORIZATION_TYPE_BASIC)) {
                return authorization.substring(6);
            }
            return authorization;
        } catch (RuntimeException e) {
            throw new BizException(ErrorCodes.JWT_TOKEN_BAD_FORMAT, HttpStatus.UNAUTHORIZED);
        }
    }
}