package com.github.doodler.common.context;

/**
 * 
 * @Description: InstanceId
 * @Author: Fred Feng
 * @Date: 06/10/2024
 * @Version 1.0.0
 */
public interface InstanceId {

    String get();

    default boolean isStandby() {
        return false;
    }

}
