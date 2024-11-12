package com.github.doodler.common.ws.security;

import com.github.doodler.common.security.HttpSecurityCustomizer;
import com.github.doodler.common.security.MixedAuthenticationFilter;
import com.github.doodler.common.ws.WsServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Description: WsUserSecurityCustomizer
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class WsUserSecurityCustomizer implements HttpSecurityCustomizer {

	private final WsServerProperties serverConfig;
	private final UserDetailsService userDetailsService;

	@Override
	public void customize(HttpSecurity http) {
		http.addFilterAfter(new WsUserSecurityFilter(serverConfig, userDetailsService), MixedAuthenticationFilter.class);
	}
}