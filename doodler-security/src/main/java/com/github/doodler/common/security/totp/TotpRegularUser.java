package com.github.doodler.common.security.totp;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.SpringSecurityCoreVersion;

import com.github.doodler.common.security.PermissionGrantedAuthority;
import com.github.doodler.common.security.RegularUser;
import com.github.doodler.common.security.SecurityConstants;
import com.github.doodler.common.security.SecurityUtils;

/**
 * @Description: TotpRegularUser
 * @Author: Fred Feng
 * @Date: 04/10/2020
 * @Version 1.0.0
 */
@Getter
@Setter
public class TotpRegularUser extends RegularUser {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public TotpRegularUser(Long id, String username, String password, String securityKey) {
        this(id, username, password, securityKey, true);
    }

    public TotpRegularUser(Long id, String username, String password, String securityKey, boolean enabled) {
        this(id, username, password, securityKey, enabled, SecurityUtils.NO_AUTHORITIES);
    }

    public TotpRegularUser(Long id, String username, String password, String securityKey, boolean enabled,
                           Collection<PermissionGrantedAuthority> authorities) {
        this(id, username, password, securityKey, SecurityConstants.PLATFORM_WEBSITE, enabled, authorities);
    }

    public TotpRegularUser(Long id, String username, String password, String securityKey, String platform, boolean enabled,
                           Collection<PermissionGrantedAuthority> authorities) {
        this(id, username, password, securityKey, platform, enabled, false, authorities);
    }

    public TotpRegularUser(Long id, String username, String password, String securityKey, String platform, boolean enabled,
                           boolean firstLogin,
                           Collection<PermissionGrantedAuthority> authorities) {
        super(id, username, password, platform, enabled, firstLogin, authorities);
        this.securityKey = securityKey;
    }

    private final String securityKey;
}