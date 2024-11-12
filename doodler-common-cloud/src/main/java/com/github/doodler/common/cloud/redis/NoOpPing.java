package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;

/**
 * 
 * @Description: NoOpPing
 * @Author: Fred Feng
 * @Date: 05/08/2024
 * @Version 1.0.0
 */
public class NoOpPing implements Ping {

    @Override
    public boolean isAlive(ServiceInstance obj) {
        return true;
    }
}
