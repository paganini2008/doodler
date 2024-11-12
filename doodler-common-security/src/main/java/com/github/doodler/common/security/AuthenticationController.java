package com.github.doodler.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;

/**
 * @Description: AuthenticationController
 * @Author: Fred Feng
 * @Date: 09/01/2023
 * @Version 1.0.0
 */
@RequestMapping("/security")
@RestControllerEndpoint(id = "security")
public class AuthenticationController {

    @Autowired
    private PermissionAccessChecker permissionAccessChecker;

    @GetMapping("/has-role")
    public ApiResult<Boolean> hasRole(@RequestParam("role") String role) {
        return ApiResult.ok(permissionAccessChecker.hasRole(role));
    }

    @GetMapping("/has-permission")
    public ApiResult<Boolean> hasPermission(@RequestParam("permission") String permission) {
        return ApiResult.ok(permissionAccessChecker.hasPermission(permission));
    }

    @GetMapping("/whoami")
    public ApiResult<UserDetails> getUser() {
        return ApiResult.ok(SecurityUtils.getCurrentUser());
    }
}