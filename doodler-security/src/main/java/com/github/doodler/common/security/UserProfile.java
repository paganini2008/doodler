package com.github.doodler.common.security;

/**
 * @Description: UserProfile
 * @Author: Fred Feng
 * @Date: 02/10/2020
 * @Version 1.0.0
 */
public interface UserProfile {

	String getId();
	
	String getName();

	String getEmail();

	String getAvatar();
}