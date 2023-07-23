package io.doodler.feign.actuator;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import io.doodler.common.ApiResult;
import io.doodler.feign.LoadBalancerClient;
import io.doodler.feign.ServiceInstance;

/**
 * 
 * @Description: ServiceInstanceEndpoint
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
@Endpoint(id = "loadBalancer")
public class ServiceInstanceEndpoint {
	
	@Autowired
    private LoadBalancerClient loadBalancerClient;

	@ReadOperation
	public ApiResult<Map<String, Collection<ServiceInstance>>> candidates(){
		return ApiResult.ok(loadBalancerClient.candidates());
	}
	
}
