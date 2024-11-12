package com.github.doodler.common.ws;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.doodler.common.security.IdentifiableUserDetails;
import com.github.doodler.common.security.SecurityConstants;
import com.github.doodler.common.security.SecurityUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: WsUserDetails
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id", "username", "password", "enabled", "platform"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsUserDetails implements IdentifiableUserDetails {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private String platform;

    private Collection<? extends GrantedAuthority> authorities;

    private Map<String, Object> attributes = new HashMap<>();

    public WsUserDetails(IdentifiableUserDetails identifiableUserDetails) {
        this.id = identifiableUserDetails.getId();
        this.username = identifiableUserDetails.getUsername();
        this.password = identifiableUserDetails.getPassword();
        this.enabled = identifiableUserDetails.isEnabled();
        this.platform = identifiableUserDetails.getPlatform();

        this.authorities = CollectionUtils.isNotEmpty(identifiableUserDetails.getAuthorities())
                ? SecurityUtils.getSimpleAuthorities(identifiableUserDetails.getAuthorities())
                : Collections.emptyList();
        this.attributes = identifiableUserDetails.getAdditionalInformation();
    }

    public WsUserDetails(Long id, String username, String password, boolean enabled,
            String platform, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.platform = platform;
        this.authorities = CollectionUtils.isNotEmpty(authorities)
                ? SecurityUtils.getSimpleAuthorities(authorities)
                : Collections.emptyList();
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
    public String getAuthorizationType() {
        return SecurityConstants.AUTHORIZATION_TYPE_BEARER;
    }
}
