package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.serviceregistry.Registration;

/**
 * 
 * @Description: ServiceRegistration
 * @Author: Fred Feng
 * @Date: 09/08/2024
 * @Version 1.0.0
 */
public interface ServiceRegistration extends Registration {

    int getActuatorPort();

    String getExternalHost();

    String getContextPath();

    String getActuatorContextPath();

    default int getWeight() {
        return 1;
    }

    default boolean isEnabled() {
        return true;
    }

    default boolean isRegisterSelf() {
        return true;
    }

    Object getConfiguration();

}
