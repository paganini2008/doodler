package com.github.doodler.common.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 
 * @Description: GoogleUserInfo
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
public class GoogleUserInfo extends OAuth2UserInfo {

    public GoogleUserInfo(OAuth2User oAuth2User) {
        super(oAuth2User);
    }

    @Override
    public String getExternalId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAvatar() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getRegistrationId() {
        return "google";
    }
}
