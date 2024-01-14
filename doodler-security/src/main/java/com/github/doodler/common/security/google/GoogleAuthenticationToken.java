package com.github.doodler.common.security.google;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;

import com.github.doodler.common.security.PlatformToken;
import com.github.doodler.common.security.SocialAccountAuthenticationToken;

/**
 * @Description: GoogleAuthenticationToken
 * @Author: Fred Feng
 * @Date: 30/07/2020
 * @Version 1.0.0
 */
public class GoogleAuthenticationToken extends SocialAccountAuthenticationToken implements PlatformToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	public GoogleAuthenticationToken(Object principal, String accessToken, String channel) {
		this(principal, accessToken, channel, AuthorityUtils.NO_AUTHORITIES);
	}

	public GoogleAuthenticationToken(Object principal, String accessToken, String channel,
	                                 Collection<? extends GrantedAuthority> authorities) {
		super(channel, authorities);
		this.principal = principal;
		this.accessToken = accessToken;
		super.setAuthenticated(true);
	}

	private final Object principal;
	private final String accessToken;

	@Override
	public Object getCredentials() {
		return accessToken;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}