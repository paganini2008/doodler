package com.github.doodler.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: SimpleGrantedAuthority
 * @Author: Fred Feng
 * @Date: 25/12/2022
 * @Version 1.0.0
 */
public class SimpleGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	@Setter
	@Getter
	private String authority;
	
	public SimpleGrantedAuthority() {
	}

	public SimpleGrantedAuthority(String authority) {
		Assert.hasText(authority, "A granted authority textual representation is required");
		this.authority = authority;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SimpleGrantedAuthority) {
			return this.authority.equals(((SimpleGrantedAuthority) obj).authority);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.authority.hashCode();
	}

	@Override
	public String toString() {
		return this.authority;
	}
}