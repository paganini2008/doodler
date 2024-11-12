package com.github.doodler.common.utils;

import java.beans.PropertyDescriptor;

/**
 * @Description: PropertyFilter
 * @Author: Fred Feng
 * @Date: 23/11/2022
 * @Version 1.0.0
 */
public interface PropertyFilter {

	boolean accept(Class<?> delaringClass, String name, PropertyDescriptor descriptor);

	static PropertyFilter disjunction() {
		return (type, name, descriptor) -> {
			return false;
		};
	}

	static PropertyFilter conjunction() {
		return (type, name, descriptor) -> {
			return true;
		};
	}

	default PropertyFilter and(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) && filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter or(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) || filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter not(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return !filter.accept(type, name, descriptor);
		};
	}
	
}
