package io.doodler.feign;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import feign.Request;

/**
 * @Description: RandomLoadBalancer
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
public class RandomLoadBalancer implements LoadBalancer {

	@Override
	public ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, Request request) {
		if (apiInstances.size() == 1) {
			return apiInstances.get(0);
		}
		int index = ThreadLocalRandom.current().nextInt(apiInstances.size());
		return apiInstances.get(index);
	}
}