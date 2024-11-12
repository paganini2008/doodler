package com.github.doodler.common.security;

/**
 * @Description: PlatformToken
 * @Author: Fred Feng
 * @Date: 06/12/2022
 * @Version 1.0.0
 */
public interface PlatformToken {

    default String getPlatform() {
        return SecurityConstants.PLATFORM_WEBSITE;
    }
}
