package com.github.doodler.common.security.oauth2;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 
 * @Description: OAuth2UserInfo
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
public abstract class OAuth2UserInfo implements UserInfo {

    protected final Map<String, Object> attributes;
    protected final @Nullable Object principal;

    public OAuth2UserInfo(OAuth2User oAuth2User) {
        this(oAuth2User.getAttributes(), oAuth2User);
    }

    public OAuth2UserInfo(Map<String, Object> attributes, Object principal) {
        this.attributes = attributes;
        this.principal = principal;
    }

    public Object getPrincipal() {
        return principal;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("[%s] id: %s, name: %s, email: %s, avator: %s",
                getRegistrationId(), getExternalId(), getName(), getEmail(), getAvatar()));
        return str.toString();
    }

}
