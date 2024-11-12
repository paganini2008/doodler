package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;

/**
 * 
 * @Description: Ping
 * @Author: Fred Feng
 * @Date: 05/08/2024
 * @Version 1.0.0
 */
@FunctionalInterface
public interface Ping {

    boolean isAlive(ServiceInstance instance);
}
