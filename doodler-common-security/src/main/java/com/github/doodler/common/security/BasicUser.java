package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BASIC;
import java.util.Collection;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

/**
 * @Description: BasicUser
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
public class BasicUser extends User implements PlatformUserDetails {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public BasicUser(String username, String password, String platform, String... roles) {
        this(username, password, platform, true, roles);
    }

    public BasicUser(String username, String password, String platform, boolean enabled, String... roles) {
        this(username, password, platform, enabled, SecurityUtils.getGrantedAuthorities(roles));
    }

    public BasicUser(String username, String password, String platform, boolean enabled,
                     Collection<PermissionGrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.platform = platform;
    }

    private final String platform;

    @Override
    public String getAuthorizationType() {
        return AUTHORIZATION_TYPE_BASIC;
    }

    @Override
    public String getPlatform() {
        return platform;
    }
    
    @Override
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	str.append(String.format("Platform: %s\n", platform));
    	str.append(super.toString());
    	return str.toString();
    }
}