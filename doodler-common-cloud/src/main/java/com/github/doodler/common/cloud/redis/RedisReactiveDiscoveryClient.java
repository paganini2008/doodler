package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * 
 * @Description: RedisReactiveDiscoveryClient
 * @Author: Fred Feng
 * @Date: 25/12/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisReactiveDiscoveryClient implements ReactiveDiscoveryClient {

    private final ServiceInstanceManager serviceInstanceManager;

    @Override
    public String description() {
        return "Redis Reactive Discovery Client";
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Flux.fromIterable(serviceInstanceManager.getInstancesByServiceId(serviceId));
    }

    @Override
    public Flux<String> getServices() {
        return Flux.fromIterable(serviceInstanceManager.getServiceNames());
    }

}
