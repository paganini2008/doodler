package com.github.doodler.common.security.google;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.doodler.common.security.PlatformUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: GoogleAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 30/07/2020
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class GoogleAuthenticationProvider implements AuthenticationProvider {

	private final GoogleClientApiService googleAccountService;
	private final PlatformUserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		GoogleAuthenticationToken googleToken = (GoogleAuthenticationToken) authentication;
		final String channel = googleToken.getChannel();
		String identifier = (String) googleToken.getPrincipal();
		String accessToken = (String) googleToken.getCredentials();
        if (log.isTraceEnabled()) {
            log.trace("Authenticate identifier '{}' and accessToken '{}'", identifier, accessToken);
        }
		GoogleProfile userInfo = googleAccountService.getUserInfo(accessToken);
        if(userInfo == null) {
        	throw new UsernameNotFoundException("Mismatched Google user: " + identifier + ", token: " + accessToken);
        }
		UserDetails user = userDetailsService.loadUserBySocialAccount(identifier, channel);
		return new GoogleAuthenticationToken(user, accessToken, channel, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return GoogleAuthenticationToken.class.isAssignableFrom(authentication);
	}
}