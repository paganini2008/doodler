package com.github.doodler.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @Description: PropertyFilters
 * @Author: Fred Feng
 * @Date: 02/11/2023
 * @Version 1.0.0
 */
@UtilityClass
public class PropertyFilters {

    public PropertyFilter includedProperties(String... includedProperties) {
        return (clz, propertyName, pd) -> {
            return ArrayUtils.contains(includedProperties, propertyName);
        };
    }

    public PropertyFilter excludedProperties(String... excludedProperties) {
        return (clz, propertyName, pd) -> {
            return !ArrayUtils.contains(excludedProperties, propertyName);
        };
    }
}