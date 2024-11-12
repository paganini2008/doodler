package com.github.doodler.common.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 
 * @Description: GitHubUserInfo
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
public class GitHubUserInfo extends OAuth2UserInfo {

    public GitHubUserInfo(OAuth2User oAuth2User) {
        super(oAuth2User);
    }

    @Override
    public String getExternalId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAvatar() {
        return (String) attributes.get("avatar_url");
    }

    @Override
    public String getRegistrationId() {
        return "github";
    }
}
