package com.github.doodler.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: BasicUserDetailsService
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
public interface BasicUserDetailsService extends UserDetailsService {
	
	void addBasicUser(String username, String password, String platform, String...roles);
	
	void removeBasicUser(String username);
	
	void cleanBasicUsers();

    UserDetails loadBasicUserByUsername(String username) throws UsernameNotFoundException;
}