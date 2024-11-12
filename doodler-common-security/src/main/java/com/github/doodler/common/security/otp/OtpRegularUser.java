package com.github.doodler.common.security.otp;

import java.util.Collection;
import org.springframework.security.core.SpringSecurityCoreVersion;
import com.github.doodler.common.security.PermissionGrantedAuthority;
import com.github.doodler.common.security.RegularUser;
import com.github.doodler.common.security.SecurityConstants;
import com.github.doodler.common.security.SecurityUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: OtpRegularUser
 * @Author: Fred Feng
 * @Date: 23/10/2024
 * @Version 1.0.0
 */
@Getter
@Setter
public class OtpRegularUser extends RegularUser {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public OtpRegularUser(Long id, String username, String password, String email,
            String securityKey, boolean enabled) {
        this(id, username, password, email, securityKey, enabled, SecurityUtils.NO_AUTHORITIES);
    }

    public OtpRegularUser(Long id, String username, String password, String email,
            String securityKey, boolean enabled,
            Collection<PermissionGrantedAuthority> authorities) {
        this(id, username, password, email, securityKey, SecurityConstants.PLATFORM_WEBSITE,
                enabled, authorities);
    }

    public OtpRegularUser(Long id, String username, String password, String email,
            String securityKey, String platform, boolean enabled,
            Collection<PermissionGrantedAuthority> authorities) {
        super(id, username, password, platform, email, enabled, authorities);
        this.securityKey = securityKey;
    }

    private final String securityKey;
}
