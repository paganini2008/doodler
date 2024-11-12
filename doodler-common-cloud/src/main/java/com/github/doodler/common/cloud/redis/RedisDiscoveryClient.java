package com.github.doodler.common.cloud.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisDiscoveryClient
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisDiscoveryClient implements DiscoveryClient {

    private final ServiceInstanceManager serviceInstanceManager;

    @Override
    public String description() {
        return RedisDiscoveryClient.class.getName();
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return serviceInstanceManager.getInstancesByServiceId(serviceId);
    }

    @Override
    public List<String> getServices() {
        return new ArrayList<>(serviceInstanceManager.getServiceNames());
    }
}
