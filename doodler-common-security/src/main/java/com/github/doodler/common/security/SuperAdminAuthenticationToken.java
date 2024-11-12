package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.ROLE_SUPER_AMDIN;
import static com.github.doodler.common.security.SecurityConstants.SUPER_AMDIN;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * @Description: SuperAdminAuthenticationToken
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
public class SuperAdminAuthenticationToken extends AbstractAuthenticationToken implements PlatformToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public SuperAdminAuthenticationToken(String password) {
        this(SUPER_AMDIN, password);
    }

    public SuperAdminAuthenticationToken(Object principal, String password) {
        super(SecurityUtils.getGrantedAuthorities(ROLE_SUPER_AMDIN));
        this.principal = principal;
        this.password = password;
    }

    private final Object principal;
    private final String password;

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}