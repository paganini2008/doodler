package com.github.doodler.common.feign;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;

import feign.Request;

/**
 * @Description: LoadBalancerClient
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
public interface LoadBalancerClient {

	Map<String, Collection<ServiceInstance>> candidates();
	
	boolean contains(String serviceId);
	
	ServiceInstance chooseFirst(String serviceId);
	
	ServiceInstance choose(String serviceId, @Nullable Request request);
	
	@Nullable ServiceInstance currentSelected(String serviceId);

	URI reconstructURI(ServiceInstance instance, URI originalUri);

	void maintain(String serviceId, String url, boolean online);
}