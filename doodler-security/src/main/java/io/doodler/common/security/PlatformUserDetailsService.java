package io.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsService
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 */
public interface PlatformUserDetailsService extends UserDetailsService {
	
	UserDetails loadUserById(Long userId) throws UsernameNotFoundException;

    UserDetails loadUserBySocialAccount(String username, String channel) throws UsernameNotFoundException;

    UserDetails loadUserByUsernameAndPlatform(String username, String platform) throws UsernameNotFoundException;
}