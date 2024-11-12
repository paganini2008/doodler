package com.github.doodler.common.cloud.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.doodler.common.cloud.ServiceInstance;

/**
 * @Description: RobinLoadBalancer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class RobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, Object parameter) {
        if (apiInstances.size() == 1) {
            return apiInstances.get(0);
        }
        int index = (int) (counter.getAndIncrement() & 0x7FFFFFFF % apiInstances.size());
        return apiInstances.get(index);
    }
}