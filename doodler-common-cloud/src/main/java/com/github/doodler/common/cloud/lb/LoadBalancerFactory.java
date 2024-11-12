package com.github.doodler.common.cloud.lb;

import org.springframework.lang.Nullable;

/**
 * 
 * @Description: LoadBalancerFactory
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
public interface LoadBalancerFactory {

    LoadBalancer createLoadBalancer(@Nullable String lbName, Class<?> lbType);
}
