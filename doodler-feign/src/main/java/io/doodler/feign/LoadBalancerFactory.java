package io.doodler.feign;

import org.springframework.lang.Nullable;

/**
 * @Description: LoadBalancerFactory
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public interface LoadBalancerFactory {

	LoadBalancer createLoadBalancer(@Nullable String lbName, Class<?> lbType);
}