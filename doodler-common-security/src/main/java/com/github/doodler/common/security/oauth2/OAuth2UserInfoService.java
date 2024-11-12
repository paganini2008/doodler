package com.github.doodler.common.security.oauth2;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

/**
 * 
 * @Description: OAuth2UserInfoService
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
public interface OAuth2UserInfoService {

    void registerNewUser(OAuth2UserInfo userInfo, OAuth2UserRequest oAuth2UserRequest);

    default void updateExistingUser(OAuth2UserInfo userInfo, UserDetails userDetails,
            OAuth2UserRequest oAuth2UserRequest) {}

}
