package com.github.doodler.common.security;

import org.slf4j.Marker;
import com.github.doodler.common.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: LoggingJwtAuthenticationFailureListener
 * @Author: Fred Feng
 * @Date: 28/11/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LoggingJwtAuthenticationFailureListener implements JwtAuthenticationFailureListener {

	private final Marker marker;

	@Override
	public void onAuthenticationFailed(JwtAuthenticationException e) {
		if (log.isWarnEnabled()) {
			Throwable cause = e.getCause();
			log.warn(marker, "Invalid Jwt Token: {}, reason: {}", e.getToken(),
					(cause instanceof BizException) ? ((BizException) cause).getErrorCode() : "<NONE>");
		}
	}
}