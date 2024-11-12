package com.github.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Description: PlatformUserDetails
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
public interface PlatformUserDetails extends UserDetails {

    default String getAuthorizationType() {
        return SecurityConstants.AUTHORIZATION_TYPE_BEARER;
    }

    String getPlatform();
}
