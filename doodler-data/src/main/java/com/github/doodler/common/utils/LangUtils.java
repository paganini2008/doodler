package com.github.doodler.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

import lombok.experimental.UtilityClass;

/**
 * @Description: LangUtils
 * @Author: Fred Feng
 * @Date: 03/03/2021
 * @Version 1.0.0
 */
@UtilityClass
public class LangUtils {

	public Object[] toObjectArray(Object obj) {
		if (obj == null) {
			return new Object[0];
		}
		if (obj.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return (Object[]) obj;
			}
			Type arrayType = obj.getClass().getComponentType();
			if (arrayType == boolean.class) {
				return ArrayUtils.toObject((boolean[]) obj);
			} else if (arrayType == char.class) {
				return ArrayUtils.toObject((char[]) obj);
			} else if (arrayType == byte.class) {
				return ArrayUtils.toObject((byte[]) obj);
			} else if (arrayType == short.class) {
				return ArrayUtils.toObject((short[]) obj);
			} else if (arrayType == int.class) {
				return ArrayUtils.toObject((int[]) obj);
			} else if (arrayType == long.class) {
				return ArrayUtils.toObject((long[]) obj);
			} else if (arrayType == float.class) {
				return ArrayUtils.toObject((float[]) obj);
			} else if (arrayType == double.class) {
				return ArrayUtils.toObject((double[]) obj);
			}
		} else if (obj instanceof Iterable) {
			List<Object> list = new ArrayList<>();
			for (Object o : (Iterable<?>) obj) {
				list.add(o);
			}
			return list.toArray();
		}
		return new Object[] { obj };
	}

	public Class<?> findParameterizedType(Class<?> implementationClass, Class<?> interfaceClass) {
		Type[] types = findParameterizedTypes(implementationClass, interfaceClass);
		return (Class<?>) types[0];
	}

	public Type[] findParameterizedTypes(Class<?> implementationClass, Class<?> interfaceClass) {
		List<ParameterizedType> parameterizedTypes = getAllParameterizedTypes(implementationClass);
		for (ParameterizedType parameterizedType : parameterizedTypes) {
			if (parameterizedType.getRawType() == interfaceClass) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				return actualTypeArguments;
			}
		}
		throw new IllegalStateException(implementationClass.getName());
	}

	public List<ParameterizedType> getAllParameterizedTypes(Class<?> objectClass) {
		Assert.notNull(objectClass, "ObjectClass must not be null");
		List<ParameterizedType> parameterizedTypesFound = new ArrayList<ParameterizedType>();
		getAllParameterizedTypes(objectClass, parameterizedTypesFound);
		return parameterizedTypesFound;
	}

	private void getAllParameterizedTypes(Class<?> objectClass, List<ParameterizedType> parameterizedTypesFound) {
		while (objectClass != null) {
			Type[] types = objectClass.getGenericInterfaces();
			for (int i = 0; i < types.length; i++) {
				if (types[i] instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) types[i];
					if (!parameterizedTypesFound.contains(parameterizedType)) {
						parameterizedTypesFound.add(parameterizedType);
						getAllParameterizedTypes((Class<?>) parameterizedType.getRawType(), parameterizedTypesFound);
					}
				} else {
					getAllParameterizedTypes((Class<?>) types[i], parameterizedTypesFound);
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

	public <T extends Comparable<T>> int compareTo(T left, T right) {
		if (left == null && right != null) {
			return -1;
		} else if (left != null && right == null) {
			return 1;
		} else if (left != null && right != null) {
			return compareResult(left.compareTo(right));
		}
		return 0;
	}

	private <T extends Comparable<T>> int compareResult(int result) {
		if (result < 0) {
			return -1;
		}
		if (result > 0) {
			return 1;
		}
		return 0;
	}
}