package com.github.doodler.common.utils;

/**
 * @Description: RemovalListener
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public interface RemovalListener<V> {

    default void onRemoval(Object elderKey, V elderValue) {
    }

    default void onRemoval(V elderValue) {
    }
}