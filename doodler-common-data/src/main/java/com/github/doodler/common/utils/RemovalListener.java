package com.github.doodler.common.utils;

/**
 * @Description: RemovalListener
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface RemovalListener<V> {

    void onRemoval(Object elderKey, V elderValue);
}
