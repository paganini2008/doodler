package com.github.doodler.common.utils;

import java.util.Observer;

/**
 * @Description: MutableObserver
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public interface MutableObserver extends Observer {

    default boolean isPrimary() {
        return false;
    }
}