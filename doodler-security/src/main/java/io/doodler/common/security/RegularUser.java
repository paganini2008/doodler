package io.doodler.common.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: RegularUser
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
@JsonIgnoreProperties("authorities")
@Getter
@Setter
public class RegularUser extends User implements IdentifiableUserDetails {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public RegularUser(Long id, String username, String password, String platform) {
        this(id, username, password, platform, true);
    }

    public RegularUser(Long id, String username, String password, String platform, boolean enabled) {
        this(id, username, password, platform, enabled, SecurityUtils.NO_AUTHORITIES);
    }

    public RegularUser(Long id, String username, String password, String platform, boolean enabled,
                       Collection<PermissionGrantedAuthority> authorities) {
        this(id, username, password, platform, enabled, false, authorities);
    }

    public RegularUser(Long id, String username, String password, String platform, boolean enabled, boolean firstLogin,
                       Collection<PermissionGrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.id = id;
        this.platform = platform;
        this.firstLogin = firstLogin;
        this.attributes = new HashMap<>();
    }

    private final Long id;
    private final String platform;
    private final boolean firstLogin;
    private final Map<String, Object> attributes;

    public String[] getRoles() {
        return getAuthorities().stream().map(au -> au.getAuthority()).toArray(l -> new String[l]);
    }

    public String[] getPermissions() {
        return getAuthorities().stream().flatMap(
                        au -> Arrays.stream(((PermissionGrantedAuthority) au).getPermissions())).distinct()
                .toArray(l -> new String[l]);
    }

    public List<PermissionGrantedAuthority> getPermissionGrantedAuthorities() {
        return getAuthorities().stream().map(au -> (PermissionGrantedAuthority) au).collect(Collectors.toList());
    }
    
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	str.append(String.format("Id: %s, Platform: %s, FirstLogin: %s\n", id, platform, firstLogin));
    	str.append(super.toString());
    	return str.toString();
    }
}