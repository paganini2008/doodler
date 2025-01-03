package com.github.doodler.common.security.oauth2;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.github.doodler.common.security.SecurityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: OAuth2UserDetails
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
@Getter
@RequiredArgsConstructor
public class OAuth2UserDetails implements OAuth2User, OAuth2UserInfoAware {

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

}
