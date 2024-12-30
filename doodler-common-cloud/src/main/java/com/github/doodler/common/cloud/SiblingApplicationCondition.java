package com.github.doodler.common.cloud;

import org.springframework.cloud.client.ServiceInstance;

/**
 * 
 * @Description: SiblingApplicationCondition
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public interface SiblingApplicationCondition {

    default boolean isSiblingApplication(ServiceInstance self, ServiceInstance other) {
        throw new UnsupportedOperationException("isSiblingApplication");
    }

    boolean isSiblingApplication(ApplicationInfo self, ApplicationInfo other);

}
