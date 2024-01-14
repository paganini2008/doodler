package com.github.doodler.common.security;

/**
 * @Description: AuthenticationPostHandler
 * @Author: Fred Feng
 * @Date: 18/07/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface AuthenticationPostHandler {

	void postAuthenticate(IdentifiableUserDetails userDetails);
}