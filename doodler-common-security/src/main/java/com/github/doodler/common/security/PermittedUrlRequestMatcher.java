package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: PermittedUrlRequestMatcher
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class PermittedUrlRequestMatcher implements RequestMatcher {

	private final PathMatcher pathMatcher = new AntPathMatcher();
	private final SecurityClientProperties config;
	private final String servletContextPath;

	@Override
	public boolean matches(HttpServletRequest request) {
		return config.getPermittedUrls().stream().anyMatch(url -> doEqualOrMatch(url, request.getRequestURI()));
	}

	private boolean doEqualOrMatch(String pathPattern, String path) {
		return (servletContextPath + pathPattern).equals(path) || pathMatcher.match(servletContextPath + pathPattern, path);
	}

}
