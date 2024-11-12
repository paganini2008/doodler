package com.github.doodler.common.cloud.lb;

import java.util.List;

import org.springframework.lang.Nullable;
import com.github.doodler.common.cloud.ServiceInstance;

/**
 * @Description: LoadBalancer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public interface LoadBalancer {

    ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, @Nullable Object parameter);
}