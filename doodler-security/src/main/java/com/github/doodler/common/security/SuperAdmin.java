package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.NA;
import static com.github.doodler.common.security.SecurityConstants.PLATFORM_ADMIN;
import static com.github.doodler.common.security.SecurityConstants.ROLE_SUPER_AMDIN;
import static com.github.doodler.common.security.SecurityConstants.SUPER_AMDIN;

import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * @Description: SuperAdmin
 * @Author: Fred Feng
 * @Date: 12/10/2020
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