package io.doodler.common.utils;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.convert.ConversionService;

/**
 * @Description: MapUtils
 * @Author: Fred Feng
 * @Date: 20/12/2022
 * @Version 1.0.0
 */
@UtilityClass
public class MapUtils {

	public boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	public Map<String, Object> linkedCaseInsensitiveMap() {
		return new CaseInsensitiveMap<>(new LinkedHashMap<>());
	}

	public Map<String, Object> concurrentCaseInsensitiveMap() {
		return new CaseInsensitiveMap<>(new ConcurrentHashMap<>());
	}

	public <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> supplier) {
		if (map == null) {
			return null;
		}
		V value = map.get(key);
		if (value == null && supplier != null) {
			synchronized (supplier) {
				value = map.get(key);
				if (value == null) {
					map.putIfAbsent(key, supplier.get());
				}
			}
			value = map.get(key);
		}
		return value;
	}

	public Map<String, String> splitAsMap(String spec, String delimiter, String subDelimiter) {
		if (StringUtils.isBlank(spec)) {
			return Collections.emptyMap();
		}
		return Arrays.stream(spec.split(delimiter)).map(arg -> arg.split(subDelimiter)).collect(HashMap::new,
				(m, args) -> m.put(args[0], args[1]), HashMap::putAll);
	}

	public <K, V> Map<K, V> retainAll(Map<K, V> left, Collection<K> keys) {
		if (left == null || left.isEmpty() || keys == null || keys.isEmpty()) {
			return left;
		}
		Map<K, V> result = new HashMap<>(left);
		for (Map.Entry<K, V> e : left.entrySet()) {
			if (!keys.contains(e.getKey())) {
				result.remove(e.getKey());
			}
		}
		return result;
	}

	/**
	 * 对象转map, 并且属性驼峰转下划线
	 *
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 */
	public Map<String, Object> obj2Map(Object obj) {
		return obj2Map(obj, true);
	}

	/**
	 * 对象转map, 并且属性驼峰转下划线
	 *
	 * @param obj
	 * @param camel2Underline
	 * @return
	 */
	public Map<String, Object> obj2Map(Object obj, boolean camel2Underline) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		if (obj == null) {
			return resultMap;
		}
		Field[] allFields = FieldUtils.getAllFields(obj.getClass());
		for (Field field : allFields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			try {
				Object value = field.get(obj);
				if (value != null) {
					resultMap.put(
							camel2Underline ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) : field.getName(),
							value);
				}
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		return resultMap;
	}

	public <T> T map2Obj(Map<String, Object> map, Class<T> clazz) {
		return map2Obj(map, clazz, null);
	}

	public <T> T map2Obj(Map<String, Object> map, Class<T> clazz, ConversionService conversionService) {
		if (map == null) {
			return null;
		}
		T obj = null;
		try {
			obj = ConstructorUtils.invokeConstructor(clazz);
			Field[] fields = FieldUtils.getAllFields(clazz);
			String key;
			Object value;
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
				value = map.get(key);
				if (conversionService != null) {
					if (value != null) {
						try {
							value = field.getType().cast(value);
						} catch (ClassCastException e) {
							value = conversionService.convert(value, field.getType());
						}
					}
				}
				field.setAccessible(true);
				field.set(obj, value);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return obj;
	}

	public List<String> matchKeys(Map<String, ?> map, String pattern, MatchMode matchMode) {
		return map.entrySet().stream().filter(e -> matchMode.matches(e.getKey(), pattern)).map(e -> e.getKey())
				.collect(Collectors.toList());
	}

	public <V> Map<String, V> matchValues(Map<String, V> map, String pattern, MatchMode matchMode) {
		return map.entrySet().stream().filter(e -> matchMode.matches(e.getKey(), pattern)).collect(LinkedHashMap::new,
				(m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
	}
}