package com.github.doodler.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @Description: NoOpHttpSecurityCustomizer
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
public class NoOpHttpSecurityCustomizer implements HttpSecurityCustomizer {

	@Override
	public void customize(HttpSecurity http) {
	}
}