package com.github.doodler.common.feign;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import feign.Request;

/**
 * @Description: RobinLoadBalancer
 * @Author: Fred Feng
 * @Date: 29/03/2021
 * @Version 1.0.0
 */
public class RobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, Request request) {
        if (apiInstances.size() == 1) {
            return apiInstances.get(0);
        }
        int index = (int) (counter.getAndIncrement() & 0x7FFFFFFF % apiInstances.size());
        return apiInstances.get(index);
    }
}