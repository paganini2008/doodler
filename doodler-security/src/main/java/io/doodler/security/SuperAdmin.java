package io.doodler.security;

import static io.doodler.security.SecurityConstants.NA;
import static io.doodler.security.SecurityConstants.PLATFORM_ADMIN;
import static io.doodler.security.SecurityConstants.ROLE_SUPER_AMDIN;
import static io.doodler.security.SecurityConstants.SUPER_AMDIN;

import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * @Description: SuperAdmin
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
public class SuperAdmin extends RegularUser {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    static final SuperAdmin INSTANCE = new SuperAdmin();

    private SuperAdmin() {
        super(0x7FFFFFFFL, SUPER_AMDIN, NA, PLATFORM_ADMIN, true,
                SecurityUtils.getGrantedAuthorities(ROLE_SUPER_AMDIN));
    }
}