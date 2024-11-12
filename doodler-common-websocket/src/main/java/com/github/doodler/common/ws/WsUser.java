package com.github.doodler.common.ws;

import org.springframework.lang.Nullable;
import com.github.doodler.common.security.IdentifiableUserDetails;

/**
 * @Description: WsUser
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
public interface WsUser {

    String getChannel();

    String getSessionId();

    @Nullable IdentifiableUserDetails getUserDetails();
}