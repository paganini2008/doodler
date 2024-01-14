package com.github.doodler.common.feign;

import org.springframework.lang.Nullable;

/**
 * @Description: LoadBalancerFactory
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
public interface LoadBalancerFactory {

	LoadBalancer createLoadBalancer(@Nullable String lbName, Class<?> lbType);
}