package com.github.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsService
 * @Author: Fred Feng
 * @Date: 21/10/2020
 * @Version 1.0.0
 */
public interface PlatformUserDetailsService extends BasicUserDetailsService {
	
	UserDetails loadUserById(Long userId) throws UsernameNotFoundException;

    UserDetails loadUserBySocialAccount(String username, String channel) throws UsernameNotFoundException;

    UserDetails loadUserByUsernameAndPlatform(String username, String platform) throws UsernameNotFoundException;
}