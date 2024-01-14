package com.github.doodler.common.feign;

import java.util.List;

import org.springframework.lang.Nullable;

import feign.Request;

/**
 * @Description: LoadBalancer
 * @Author: Fred Feng
 * @Date: 29/03/2021
 * @Version 1.0.0
 */
public interface LoadBalancer {

	ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, @Nullable Request request);
}