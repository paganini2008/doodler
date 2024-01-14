package com.github.doodler.common.ws;

import org.springframework.lang.Nullable;

import com.github.doodler.common.security.IdentifiableUserDetails;

/**
 * @Description: WsUser
 * @Author: Fred Feng
 * @Date: 10/03/2021
 * @Version 1.0.0
 */
public interface WsUser {

    String getChannel();

    String getSessionId();

    @Nullable IdentifiableUserDetails getUserDetails();
}