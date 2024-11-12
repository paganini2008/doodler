package com.github.doodler.common.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: BasicUserAuthenticationToken
 * @Author: Fred Feng
 * @Date: 25/11/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public class BasicUserAuthenticationToken extends UsernamePasswordAuthenticationToken
        implements PlatformToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public BasicUserAuthenticationToken(Object principal, String credentials, String platform,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.platform = platform;
    }

    private String platform;

    @Override
    public String getPlatform() {
        return platform;
    }
}
