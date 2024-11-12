package com.github.doodler.common.security.oauth2;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.github.doodler.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: OidcUserDetails
 * @Author: Fred Feng
 * @Date: 21/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OidcUserDetails implements OidcUser, OAuth2UserInfoAware {

    private final OAuth2UserInfo oAuth2UserInfo;
    private final @Nullable UserDetails userDetails;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails != null ? userDetails.getAuthorities() : SecurityUtils.NO_AUTHORITIES;
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getIdentity();
    }

    @Override
    public Map<String, Object> getClaims() {
        return ((OidcUser) oAuth2UserInfo.getPrincipal()).getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return ((OidcUser) oAuth2UserInfo.getPrincipal()).getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return ((OidcUser) oAuth2UserInfo.getPrincipal()).getIdToken();
    }

    @Override
    public OAuth2UserInfo getOAuth2UserInfo() {
        return oAuth2UserInfo;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

}
