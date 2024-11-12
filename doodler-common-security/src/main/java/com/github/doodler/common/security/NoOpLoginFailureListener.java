package com.github.doodler.common.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: NoOpLoginFailureListener
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 */
public class NoOpLoginFailureListener implements LoginFailureListener {

	@Override
	public void onTryAgain(AuthenticationException e) {
	}

	@Override
	public void onDisabled(AuthenticationException e) {
	}

	@Override
	public void onLocked(AuthenticationException e) {
	}
}