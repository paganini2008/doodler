package com.github.doodler.common.security;

import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * @Description: IdentifiableUserDetails
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
public interface IdentifiableUserDetails extends PlatformUserDetails {
	
	default boolean isFirstLogin() {
		return false;
	}

	@Nullable Long getId();
	
	Map<String, Object> getAttributes();
}