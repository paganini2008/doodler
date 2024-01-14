package com.github.doodler.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 
 * @Description: HttpSecurityCustomizer
 * @Author: Fred Feng
 * @Date: 15/01/2020
 * @Version 1.0.0
 */
@FunctionalInterface
public interface HttpSecurityCustomizer {

	void customize(HttpSecurity http);
}