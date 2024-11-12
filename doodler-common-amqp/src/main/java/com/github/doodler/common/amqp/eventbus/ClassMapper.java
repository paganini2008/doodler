package com.github.doodler.common.amqp.eventbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: ClassMapper
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
public class ClassMapper {

	private final Map<String, String> mapper = new ConcurrentHashMap<>();

	public void registerTypeName(String fromType, String toType) {
		mapper.put(fromType, toType);
	}

	public String getToTypeName(String fromType) {
		return mapper.get(fromType);
	}
	
	public boolean hasRegisteredType(String toType) {
		return mapper.containsValue(toType);
	}
	
	public int size() {
		return mapper.size();
	}
	
	public void clear() {
		mapper.clear();
	}
}