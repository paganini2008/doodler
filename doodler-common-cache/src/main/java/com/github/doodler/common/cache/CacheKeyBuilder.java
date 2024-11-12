package com.github.doodler.common.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import com.github.doodler.common.cache.feign.RestClientKeyGenerator;

/**
 * @Description: CacheKeyBuilder
 * @Author: Fred Feng
 * @Date: 09/02/2023
 * @Version 1.0.0
 */
@Getter
public class CacheKeyBuilder {

	private String className;
	private String methodName;
	private List<Object> arguments = new ArrayList<>();
	private final StringKeyGenerator keyGenerator;

	CacheKeyBuilder(StringKeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	public CacheKeyBuilder setClassName(Class<?> c) {
		this.className = c.getSimpleName();
		return this;
	}

	public CacheKeyBuilder setClassName(String className) {
		this.className = className;
		return this;
	}

	public CacheKeyBuilder setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

	public CacheKeyBuilder setArguments(List<Object> arguments) {
		this.arguments = arguments;
		return this;
	}

	public CacheKeyBuilder addArguments(Object... arguments) {
		if (ArrayUtils.isNotEmpty(arguments)) {
			this.arguments.addAll(Arrays.asList(arguments));
		}
		return this;
	}

	public String build() {
		return keyGenerator.generate(className, methodName, arguments.toArray());
	}

	public static CacheKeyBuilder restClient() {
		return new CacheKeyBuilder(new RestClientKeyGenerator());
	}

	public static CacheKeyBuilder generic() {
		return new CacheKeyBuilder(new GenericKeyGenerator());
	}
}