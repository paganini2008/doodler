package com.github.doodler.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @Description: PathMatcherMap
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public class PathMatcherMap<V> extends KeyMatchedMap<String, V> {

	private static final long serialVersionUID = 6889862975777381867L;

	public PathMatcherMap() {
		super(new ConcurrentHashMap<>(), false);
	}

	private final PathMatcher pathMather = new AntPathMatcher();

	@Override
	protected boolean match(String pathPattern, Object inputKey) {
		String path = (String) inputKey;
		return pathPattern.equals(path) || pathMather.match(pathPattern, path);
	}
}