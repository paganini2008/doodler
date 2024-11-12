package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.ROLE_SUPER_AMDIN;
import static com.github.doodler.common.security.SecurityConstants.SUPER_AMDIN;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: AuthenticationInfo
 * @Author: Fred Feng
 * @Date: 18/11/2022
 * @Version 1.0.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthenticationInfo {

    private Long id;
    private String username;
    private String email;
    private String platform;
    private LocalDateTime loginTime;
    private String ipAddress;
    private GrantedAuthorityInfo[] grantedAuthorities;
    private boolean firstLogin;
    private Map<String, Object> additionalInformation;

    public AuthenticationInfo(Long id, String username, String email, String platform,
            String ipAddress, GrantedAuthorityInfo[] grantedAuthorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.platform = platform;
        this.ipAddress = ipAddress;
        this.grantedAuthorities = grantedAuthorities;
        this.loginTime = LocalDateTime.now();
    }

    public boolean hasSa() {
        return SUPER_AMDIN.equals(username)
                && (grantedAuthorities != null && grantedAuthorities.length == 1
                        && ROLE_SUPER_AMDIN.equals(grantedAuthorities[0].getRole()));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class GrantedAuthorityInfo {

        private String role;
        private String[] permissions;
        private String[] opTypes;

    }
}
