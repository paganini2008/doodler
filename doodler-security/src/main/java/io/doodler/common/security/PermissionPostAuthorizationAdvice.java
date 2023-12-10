package io.doodler.common.security;

import io.doodler.common.context.HttpRequestContextHolder;
import io.doodler.common.context.HttpRequestInfo;
import io.doodler.common.context.HttpRequestInfo.AuthInfo;
import org.apache.commons.lang3.StringUtils;

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