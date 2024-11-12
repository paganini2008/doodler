package com.github.doodler.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 
 * @Description: HttpSecurityCustomizer
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface HttpSecurityCustomizer {

	void customize(HttpSecurity http);
}