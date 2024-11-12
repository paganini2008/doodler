package com.github.doodler.common.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: BasicCredentials
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicCredentials implements UserDetails {

    private static final long serialVersionUID = -6305059139141019187L;
    private String username;
    private String password;
    private String platform;
    private String[] roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityUtils.getGrantedAuthorities(roles);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }


}
