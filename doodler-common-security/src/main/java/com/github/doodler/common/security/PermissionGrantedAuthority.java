package com.github.doodler.common.security;

import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * @Description: PermissionAccessChecker
 * @Author: Fred Feng
 * @Date: 26/11/2022
 * @Version: 1.0.0
 */
public class PermissionGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String role;
    private final String[] permissions;
    private final String[] opTypes;
    
    public PermissionGrantedAuthority(String role) {
    	this(role, new String[0], new String[0]);
    }

    public PermissionGrantedAuthority(String role, @Nullable String[] permissions, @Nullable String[] opTypes) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
        this.permissions = permissions;
        this.opTypes = opTypes;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public String[] getPermissions() {
        return this.permissions;
    }

    public String[] getOpTypes() {
        return opTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PermissionGrantedAuthority) {
            PermissionGrantedAuthority other = (PermissionGrantedAuthority) obj;
            return this.role.equals(other.role) && Arrays.deepEquals(this.permissions, other.permissions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode() + Arrays.deepHashCode(this.permissions);
    }

    @Override
    public String toString() {
        return this.role + ": " + Arrays.toString(this.permissions);
    }
}