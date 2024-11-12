package com.github.doodler.common.security.oauth2;

import java.util.Collection;
import org.springframework.security.core.SpringSecurityCoreVersion;
import com.github.doodler.common.security.PermissionGrantedAuthority;
import com.github.doodler.common.security.RegularUser;
import lombok.Getter;

/**
 * 
 * @Description: OAuth2ClientUser
 * @Author: Fred Feng
 * @Date: 20/10/2024
 * @Version 1.0.0
 */
@Getter
public class OAuth2ClientUser extends RegularUser {

    public OAuth2ClientUser(Long id, String username, String password, String email,
            String platform, String registrationId, boolean enabled,
            Collection<PermissionGrantedAuthority> authorities) {
        super(id, username, password, email, platform, enabled, authorities);
        this.registrationId = registrationId;
    }

    private final String registrationId;

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;


}
