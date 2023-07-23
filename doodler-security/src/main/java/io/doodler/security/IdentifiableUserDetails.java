package io.doodler.security;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Description: IdentifiableUserDetails
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
public interface IdentifiableUserDetails extends UserDetails {
	
	default String getAuthorizationType() {
		return SecurityConstants.AUTHORIZATION_TYPE_BEARER;
	}
	
	default boolean isFirstLogin() {
		return false;
	}

	@Nullable Long getId();
	
	String getPlatform();
	
	Map<String, Object> getAttributes();
}