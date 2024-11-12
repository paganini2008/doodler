package com.github.doodler.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.experimental.UtilityClass;

/**
 * @Description: ListUtils
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class ListUtils {

	public <E> List<E> concat(Collection<E> left, Collection<E> right) {
		List<E> list = new ArrayList<>();
		if (left != null) {
			list.addAll(left);
		}
		if (right != null) {
			list.addAll(right);
		}
		return list;
	}
	
	public static <T> T getFirst(List<T> list) {
		return getFirst(list, null);
	}

	public static <T> T getFirst(List<T> list, T defaultValue) {
		if (CollectionUtils.isEmpty(list)) {
			return defaultValue;
		}
		return list.get(0);
	}
}