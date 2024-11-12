package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.NA;
import static com.github.doodler.common.security.SecurityConstants.PLATFORM_ADMIN;
import static com.github.doodler.common.security.SecurityConstants.ROLE_SUPER_AMDIN;
import static com.github.doodler.common.security.SecurityConstants.SUPER_AMDIN;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 
 * @Description: SuperAdmin
 * @Author: Fred Feng
 * @Date: 23/10/2024
 * @Version 1.0.0
 */
public class SuperAdmin extends RegularUser {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    static final SuperAdmin INSTANCE = new SuperAdmin();

    SuperAdmin() {
        this(SUPER_AMDIN, NA, null);
    }

    SuperAdmin(String username, String password, String email) {
        super(null, username, password, null, PLATFORM_ADMIN, true,
                SecurityUtils.getGrantedAuthorities(ROLE_SUPER_AMDIN));
    }
}
