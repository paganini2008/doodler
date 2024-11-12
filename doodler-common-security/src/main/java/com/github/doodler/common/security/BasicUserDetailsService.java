package com.github.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * @Description: BasicUserDetailsService
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
public interface BasicUserDetailsService
        extends UserDetailsService, UserDetailsManager, UserDetailsPasswordService {

    void cleanUsers();

    UserDetails loadBasicUserByUsername(String username) throws UsernameNotFoundException;
}
