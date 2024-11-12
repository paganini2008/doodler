package com.github.doodler.common.security.oauth2;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 
 * @Description: SimpleOAuth2ClientAuthenticationToken
 * @Author: Fred Feng
 * @Date: 23/10/2024
 * @Version 1.0.0
 */
public class SimpleOAuth2ClientAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public SimpleOAuth2ClientAuthenticationToken(Object principal, String accessToken,
            String source, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.accessToken = accessToken;
        this.source = source;
    }

    private final Object principal;
    private final String accessToken;
    private final String source;

    public String getSource() {
        return source;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
