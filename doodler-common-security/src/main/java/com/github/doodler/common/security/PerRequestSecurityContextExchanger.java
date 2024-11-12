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
public class PerRequestSecurityContextExchanger implements RequestContextExchanger {

    @Override
    public Object get() {
        return SecurityContextHolder.getContext();
    }

    @Override
    public void set(Object obj) {
        SecurityContext sc = (SecurityContext) obj;
        if (sc != null && sc.getAuthentication() != null) {
            SecurityContextHolder.setContext(sc);
        }
    }

    @Override
    public void reset() {
        SecurityContextHolder.clearContext();
    }
}