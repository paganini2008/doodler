package com.github.doodler.common.webmvc;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.github.doodler.common.enums.EnumConstant;

/**
 * @Description: EnumDeclarations
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
public abstract class EnumDeclarations {

	private static final Map<String, EnumConstant[]> enums = new HashMap<>();

	public static <E extends EnumConstant> void registerEnums(Class<E> enumType, E[] enumConstants) {
		enums.put(Introspector.decapitalize(enumType.getSimpleName()), enumConstants);
	}

	public static void registerEnums(String name, EnumConstant[] enumConstants) {
		enums.put(name, enumConstants);
	}

	public static Map<String, Map<String, Object>> getEnums() {
		Map<String, EnumConstant[]> copy = new HashMap<>(enums);
		return copy.entrySet().stream().collect(HashMap::new,
				(m, e) -> m.put(e.getKey(),
						Arrays.stream(e.getValue()).sorted()
								.collect(Collectors.toMap(EnumConstant::getRepr, EnumConstant::getValue, (a, b) -> a, LinkedHashMap::new))),
				HashMap::putAll);
	}
}