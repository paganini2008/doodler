package io.doodler.security;

import static io.doodler.security.SecurityConstants.AUTHORIZATION_TYPE_BASIC;
import static io.doodler.security.SecurityConstants.PLATFORM_WEBSITE;
import static io.doodler.security.SecurityConstants.USER_ROLE_PLAYER;

import java.util.Collection;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * @Description: BasicUser
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
public class BasicUser extends RegularUser {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public BasicUser(Long id, String username, String password) {
        this(id, username, password, PLATFORM_WEBSITE);
    }

    public BasicUser(Long id, String username, String password, String platform) {
        this(id, username, password, platform, SecurityUtils.getGrantedAuthorities(USER_ROLE_PLAYER));
    }

    public BasicUser(Long id, String username, String password, String platform,
                     Collection<PermissionGrantedAuthority> authorities) {
        super(id, username, password, platform, true, authorities);
    }

    @Override
    public String getAuthorizationType() {
        return AUTHORIZATION_TYPE_BASIC;
    }
}