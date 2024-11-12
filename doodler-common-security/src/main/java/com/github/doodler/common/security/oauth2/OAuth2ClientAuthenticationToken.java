package com.github.doodler.common.security.oauth2;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import com.github.doodler.common.security.PlatformToken;

/**
 * 
 * @Description: OAuth2ClientAuthenticationToken
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
public class OAuth2ClientAuthenticationToken extends AbstractAuthenticationToken
        implements PlatformToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public OAuth2ClientAuthenticationToken(Object principal, Object credentials,
            OAuth2ClientType oAuth2ClientType, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.oAuth2ClientType = oAuth2ClientType;
    }

    private final Object principal;
    private final Object credentials;
    private final OAuth2ClientType oAuth2ClientType;

    public OAuth2ClientType getOAuth2ClientType() {
        return oAuth2ClientType;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
