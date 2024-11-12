package com.github.doodler.common.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.github.doodler.common.BizException;
import lombok.RequiredArgsConstructor;

/**
 * @Description: SuperAdminAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SuperAdminAuthenticationProvider implements AuthenticationProvider {

	private final SuperAdminPassword superAdminPassword;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SuperAdminAuthenticationToken authenticationToken = (SuperAdminAuthenticationToken) authentication;
		String password = (String) authenticationToken.getCredentials();
		if (!superAdminPassword.matches(password)) {
			throw new BizException(ErrorCodes.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
		}
		return new SuperAdminAuthenticationToken(SuperAdmin.INSTANCE, SecurityConstants.NA);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SuperAdminAuthenticationToken.class.isAssignableFrom(authentication);
	}
}