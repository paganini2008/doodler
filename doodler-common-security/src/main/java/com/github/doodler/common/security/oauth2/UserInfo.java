package com.github.doodler.common.security.oauth2;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @Description: UserInfo
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
public interface UserInfo {

    String getRegistrationId();

    String getExternalId();

    String getName();

    String getEmail();

    String getAvatar();

    default String getIdentity() {
        String email = getEmail();
        if (StringUtils.isNotBlank(email)) {
            return email;
        }
        String name = getName();
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return getExternalId();
    }
}
