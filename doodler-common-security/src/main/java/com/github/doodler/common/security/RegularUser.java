package com.github.doodler.common.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
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
@JsonIgnoreProperties("additionalInformation")
@Getter
@Setter
public class RegularUser extends User implements IdentifiableUserDetails {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public RegularUser(Long id, String username, String password, String email, String platform,
            boolean enabled) {
        this(id, username, password, email, platform, enabled, SecurityUtils.NO_AUTHORITIES);
    }

    public RegularUser(Long id, String username, String password, String email, String platform,
            boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.id = id;
        this.email = email;
        this.platform = platform;
        this.additionalInformation = new HashMap<>();
    }

    private final Long id;
    private final String email;
    private final String platform;
    private final Map<String, Object> additionalInformation;

    public boolean isFirstLogin() {
        return additionalInformation.containsKey("firstLogin")
                ? (Boolean) additionalInformation.get("firstLogin")
                : false;
    }

    public String[] getRoles() {
        return getAuthorities().stream().map(au -> au.getAuthority()).toArray(l -> new String[l]);
    }

    public String[] getPermissions() {
        return getAuthorities().stream()
                .flatMap(au -> Arrays.stream(((PermissionGrantedAuthority) au).getPermissions()))
                .distinct().toArray(l -> new String[l]);
    }

    public List<PermissionGrantedAuthority> getPermissionGrantedAuthorities() {
        return getAuthorities().stream().map(au -> (PermissionGrantedAuthority) au)
                .collect(Collectors.toList());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("ID=").append(getId()).append(", ");
        sb.append("Username=").append(getUsername()).append(", ");
        sb.append("Email=").append(getEmail()).append(", ");
        sb.append("Platform=").append(getPlatform()).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("FirstLogin=").append(isFirstLogin()).append(", ");
        sb.append("Enabled=").append(isEnabled()).append(", ");
        sb.append("AccountNonExpired=").append(isAccountNonExpired()).append(", ");
        sb.append("credentialsNonExpired=").append(isCredentialsNonExpired()).append(", ");
        sb.append("AccountNonLocked=").append(isAccountNonLocked()).append(", ");
        sb.append("Granted Authorities=").append(getAuthorities()).append("]");
        return sb.toString();
    }
}
