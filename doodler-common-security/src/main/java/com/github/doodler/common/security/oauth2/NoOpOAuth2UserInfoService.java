package com.github.doodler.common.security.oauth2;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: NoOpOAuth2UserInfoService
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class NoOpOAuth2UserInfoService implements OAuth2UserInfoService {

    @Override
    public void registerNewUser(OAuth2UserInfo userInfo, OAuth2UserRequest oAuth2UserRequest) {

    }

    @Override
    public void updateExistingUser(OAuth2UserInfo userInfo, UserDetails userDetails,
            OAuth2UserRequest oAuth2UserRequest) {

    }

}
