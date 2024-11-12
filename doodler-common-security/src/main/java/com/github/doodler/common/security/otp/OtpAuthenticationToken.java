package com.github.doodler.common.security.otp;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import com.github.doodler.common.security.PlatformToken;

/**
 * 
 * @Description: OtpAuthenticationToken
 * @Author: Fred Feng
 * @Date: 23/10/2024
 * @Version 1.0.0
 */
public class OtpAuthenticationToken extends AbstractAuthenticationToken implements PlatformToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public OtpAuthenticationToken(Object principal, String password, String code) {
        this(principal, password, code, AuthorityUtils.NO_AUTHORITIES);
    }

    public OtpAuthenticationToken(Object principal, String password, String code,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.password = password;
        this.code = code;
        super.setAuthenticated(true);
    }

    private final Object principal;
    private final String password;
    private final String code;

    public String getCode() {
        return code;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
