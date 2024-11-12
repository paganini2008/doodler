package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.PERMISSION_TYPE_NAME_OPERATION;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

/**
 * @Description: PermissionAccessChecker
 * @Author: Fred Feng
 * @Date: 23/11/2022
 * @Version: 1.0.0
 */
@Component("pms")
public class PermissionAccessChecker implements SmartInitializingSingleton, ApplicationContextAware {

    private final List<PostAuthorizationAdvice> postHandlers = new CopyOnWriteArrayList<>();

    public boolean hasRole(String... roles) {
        boolean hasAuthorized = doAuthorizeRoles(roles);
        postHandlers.forEach(handler -> handler.postAuthorizeRoles(hasAuthorized, roles[0]));
        return hasAuthorized;
    }

    private boolean doAuthorizeRoles(String... roles) {
        if (ArrayUtils.isEmpty(roles)) {
            return false;
        }
        if (SecurityContextHolder.getContext() == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        if (authentication instanceof PlatformToken) {
            if (authentication.getPrincipal() instanceof SuperAdmin) {
                return true;
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream().map(GrantedAuthority::getAuthority).distinct().anyMatch(
                    x -> PatternMatchUtils.simpleMatch(roles, x));
        }
        throw new PreAuthenticatedCredentialsNotFoundException(
                "Unsupported authentication type: " + authentication.getClass());
    }

    public boolean hasPermission(String... permissions) {
        boolean hasAuthorized = doAuthorizePermissions(permissions);
        postHandlers.forEach(handler -> handler.postAuthorizePermissions(hasAuthorized, permissions[0]));
        return hasAuthorized;
    }

    private boolean doAuthorizePermissions(String... permissions) {
        if (ArrayUtils.isEmpty(permissions)) {
            return false;
        }
        if (SecurityContextHolder.getContext() == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        if (authentication instanceof PlatformToken) {
            if (authentication.getPrincipal() instanceof SuperAdmin) {
                return true;
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream().flatMap(
                            au -> Arrays.stream(((PermissionGrantedAuthority) au).getPermissions())).distinct()
                    .filter(x -> StringUtils.isNotBlank(x) && x.startsWith(PERMISSION_TYPE_NAME_OPERATION))
                    .anyMatch(x -> PatternMatchUtils.simpleMatch(permissions,
                            x.replaceFirst(PERMISSION_TYPE_NAME_OPERATION, "")));
        }
        throw new PreAuthenticatedCredentialsNotFoundException(
                "Unsupported authentication type: " + authentication.getClass());
    }

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, PostAuthorizationAdvice> handlerBeans = ctx.getBeansOfType(PostAuthorizationAdvice.class);
        if (MapUtils.isNotEmpty(handlerBeans)) {
            postHandlers.addAll(handlerBeans.values());
        }
    }
}