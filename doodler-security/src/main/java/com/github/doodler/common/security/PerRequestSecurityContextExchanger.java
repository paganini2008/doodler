package com.github.doodler.common.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.doodler.common.context.RequestContextExchanger;

/**
 * @Description: PerRequestSecurityContextExchanger
 * @Author: Fred Feng
 * @Date: 08/12/2023
 * @Version 1.0.0
 */
public class PerRequestSecurityContextExchanger implements RequestContextExchanger<SecurityContext> {

    @Override
    public SecurityContext get() {
        return SecurityContextHolder.getContext();
    }

    @Override
    public void set(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null) {
            SecurityContextHolder.setContext(sc);
        }
    }

    @Override
    public void reset() {
        SecurityContextHolder.clearContext();
    }
}