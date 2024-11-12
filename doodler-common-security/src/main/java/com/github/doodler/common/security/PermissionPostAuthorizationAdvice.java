package com.github.doodler.common.security;

import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;
import com.github.doodler.common.context.HttpRequestInfo.AuthInfo;

/**
 * @Description: PermissionPostAuthorizationAdvice
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
public class PermissionPostAuthorizationAdvice implements PostAuthorizationAdvice {

    @Override
    public void postAuthorizePermissions(boolean approved, String permission) {
        HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
        if (httpRequestInfo != null && StringUtils.isNotBlank(permission)) {
            httpRequestInfo.setAuthInfo(new AuthInfo(SecurityUtils.getCurrentUser(), null, permission, approved));
        }
    }
}