package com.github.doodler.common.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * @Description: InternalAuthenticationToken
 * @Author: Fred Feng
 * @Date: 06/12/2022
 * @Version 1.0.0
 */
public class InternalAuthenticationToken extends AbstractAuthenticationToken implements PlatformToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;
    private final Object credentials;
    private final String platform;
    private final boolean rememberMe;

    public InternalAuthenticationToken(Object principal, Object credentials, String platform, boolean rememberMe) {
        this(principal, credentials, platform, rememberMe, AuthorityUtils.NO_AUTHORITIES);
    }

    public InternalAuthenticationToken(Object principal, Object credentials, String platform, boolean rememberMe,
                                Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.platform = platform;
        this.rememberMe = rememberMe;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String getPlatform() {
        return this.platform;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
}