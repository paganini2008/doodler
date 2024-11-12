package com.github.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsService
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 */
public interface PlatformUserDetailsService extends BasicUserDetailsService {

    UserDetails loadUserById(Long userId) throws UsernameNotFoundException;

    UserDetails loadUserByIdentity(String identity) throws UsernameNotFoundException;

    UserDetails loadUserByIdentityAndRegistrationId(String identity, String registrationId)
            throws UsernameNotFoundException;

    UserDetails loadUserByIdentityAndPlatform(String identity, String platform)
            throws UsernameNotFoundException;
}
