package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.LOGIN_KEY;
import static com.github.doodler.common.security.SecurityConstants.REMEMBER_ME_KEY;
import static com.github.doodler.common.security.SecurityConstants.TOKEN_KEY;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import com.github.doodler.common.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WebAppAuthenticationService
 * @Author: Fred Feng
 * @Date: 18/11/2022
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WebAppAuthenticationService implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenStrategy tokenStrategy;
    private final RedisOperations<String, Object> redisOperations;
    private final SecurityClientProperties securityClientProperties;
    private final AbstractRememberMeServices rememberMeServices;
    private final PlatformUserDetailsService platformUserDetailsService;
    private final Marker marker;
    private final List<AuthenticationAspect> authenticationAspects = new CopyOnWriteArrayList<>();

    @Override
    public String signIn(AbstractAuthenticationToken authenticationToken,
            AuthenticationPostHandler postHandler, HttpServletRequest request,
            HttpServletResponse response) {
        authenticationAspects.forEach(a -> a.onLogin(authenticationToken, request, response));
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            IdentifiableUserDetails user = (IdentifiableUserDetails) authentication.getPrincipal();
            if (postHandler != null) {
                postHandler.postAuthenticate(user);
            }
            String key = String.format(LOGIN_KEY, user.getPlatform(), user.getIdentity());
            if (cleanToken(user, request, response)) {
                authenticationAspects.forEach(
                        a -> a.onLogout(user, LogoutReason.ABNORMAL_LOGOUT, request, response));
            }
            int expiration = securityClientProperties.getExpiration();
            String token = tokenStrategy.encode(user, expiration);
            redisOperations.opsForValue().set(key, token, expiration, TimeUnit.SECONDS);
            AuthenticationInfo authInfo = new AuthenticationInfo(user.getId(), user.getUsername(),
                    user.getEmail(), user.getPlatform(), WebUtils.getIpAddr(request),
                    SecurityUtils.getGrantedAuthorities(user.getAuthorities()));
            authInfo.setFirstLogin(user.isFirstLogin());
            authInfo.setAdditionalInformation(user.getAdditionalInformation());
            key = String.format(TOKEN_KEY, user.getPlatform(), token);
            redisOperations.opsForValue().set(key, authInfo, expiration, TimeUnit.SECONDS);
            authenticationAspects
                    .forEach(a -> a.onSuccess(authenticationToken, user, request, response));
            if (securityClientProperties.isShowAuthorizationType()) {
                return user.getAuthorizationType() + " " + token;
            }
            return token;
        } catch (AuthenticationException e) {
            authenticationAspects
                    .forEach(a -> a.onFailure(authenticationToken, e, request, response));
            throw e;
        }
    }

    @Override
    public String signInAndRememberMe(AbstractAuthenticationToken authenticationToken,
            AuthenticationPostHandler postHandler, HttpServletRequest request,
            HttpServletResponse response) {
        authenticationAspects.forEach(a -> a.onLogin(authenticationToken, request, response));
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            IdentifiableUserDetails user = (IdentifiableUserDetails) authentication.getPrincipal();
            if (postHandler != null) {
                postHandler.postAuthenticate(user);
            }
            String key = String.format(LOGIN_KEY, user.getPlatform(), user.getIdentity());
            if (cleanToken(user, request, response)) {
                authenticationAspects.forEach(
                        a -> a.onLogout(user, LogoutReason.ABNORMAL_LOGOUT, request, response));
            }
            int expiration = securityClientProperties.getRememberMeDuration();
            String token = tokenStrategy.encode(user, expiration);
            redisOperations.opsForValue().set(key, token, expiration, TimeUnit.SECONDS);
            AuthenticationInfo authInfo = new AuthenticationInfo(user.getId(), user.getUsername(),
                    user.getEmail(), user.getPlatform(), WebUtils.getIpAddr(request),
                    SecurityUtils.getGrantedAuthorities(user.getAuthorities()));
            authInfo.setFirstLogin(user.isFirstLogin());
            authInfo.setAdditionalInformation(user.getAdditionalInformation());
            key = String.format(TOKEN_KEY, user.getPlatform(), token);
            redisOperations.opsForValue().set(key, authInfo, expiration, TimeUnit.SECONDS);
            request.setAttribute(REMEMBER_ME_KEY, "true");
            rememberMeServices.loginSuccess(request, response, authentication);
            authenticationAspects
                    .forEach(a -> a.onSuccess(authenticationToken, user, request, response));
            if (securityClientProperties.isShowAuthorizationType()) {
                return user.getAuthorizationType() + " " + token;
            }
            return token;
        } catch (AuthenticationException e) {
            rememberMeServices.loginFail(request, response);
            authenticationAspects
                    .forEach(a -> a.onFailure(authenticationToken, e, request, response));
            throw e;
        }
    }

    @Override
    public IdentifiableUserDetails authenticate(AbstractAuthenticationToken authenticationToken)
            throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return (IdentifiableUserDetails) authentication.getPrincipal();
    }

    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        RegularUser user = null;
        try {
            user = SecurityUtils.getCurrentUser();
            cleanToken(user, request, response);
        } finally {
            InternalAuthenticationToken authenticationToken =
                    (InternalAuthenticationToken) SecurityUtils.getAuthentication();
            if (authenticationToken.isRememberMe()) {
                rememberMeServices.logout(request, response, authenticationToken);
            }
            try {
                final UserDetails userDetails = user;
                authenticationAspects.forEach(a -> a.onLogout(userDetails,
                        LogoutReason.NORMAL_LOGOUT, request, response));
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private boolean cleanToken(IdentifiableUserDetails user, HttpServletRequest request,
            HttpServletResponse response) {
        String key = String.format(LOGIN_KEY, user.getPlatform(), user.getIdentity());
        if (redisOperations.hasKey(key)) {
            String oldToken = (String) redisOperations.opsForValue().get(key);
            redisOperations.delete(key);
            if (StringUtils.isNotBlank(oldToken)) {
                key = String.format(TOKEN_KEY, user.getPlatform(), oldToken);
                redisOperations.delete(key);
            }
            if (WebUtils.hasCookie(REMEMBER_ME_KEY)) {
                ((PlatformTokenBasedRememberMeServices) rememberMeServices).cleanCookies(request,
                        response);
            }
            if (log.isInfoEnabled()) {
                log.info(marker, "Force logout from system. Token: {}", oldToken);
            }
            return true;
        }
        return false;
    }

    @EventListener({ApplicationReadyEvent.class})
    public void registerAuthenticationAspects(ApplicationReadyEvent e) {
        Map<String, AuthenticationAspect> beanMap =
                e.getApplicationContext().getBeansOfType(AuthenticationAspect.class);
        if (MapUtils.isNotEmpty(beanMap)) {
            authenticationAspects.addAll(beanMap.values());
        }
    }

    @EventListener({RedisKeyExpiredEvent.class})
    public void handleOffline(RedisKeyExpiredEvent<?> e) {
        String expiredKey = new String(e.getSource());
        if (!expiredKey.startsWith("login:")) {
            return;
        }
        String[] args = expiredKey.split(":", 3);
        if (args.length != 3) {
            return;
        }
        final String identifier = args[2];
        try {
            UserDetails userDetails = platformUserDetailsService.loadUserByUsername(identifier);
            authenticationAspects.forEach(
                    a -> a.onLogout(userDetails, LogoutReason.SESSION_EXPIRED, null, null));
        } catch (Exception ee) {
            if (log.isErrorEnabled()) {
                log.error(ee.getMessage(), ee);
            }
        }
    }
}
