package io.doodler.common.feign.actuator;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import io.doodler.common.ApiResult;

import io.doodler.common.feign.LoadBalancerClient;
import io.doodler.common.feign.ServiceInstance;

/**
 * 
 * @Description: LoadBalancerClientEndpoint
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
@Endpoint(id = "loadBalancer")
public class LoadBalancerClientEndpoint {
	
	@Autowired
    private LoadBalancerClient loadBalancerClient;

	@ReadOperation
	public ApiResult<Map<String, Collection<ServiceInstance>>> candidates(){
		return ApiResult.ok(loadBalancerClient.candidates());
	}
	
	@WriteOperation
	public ApiResult<String> maintain(@Selector String serviceId, @Selector String host,@Selector int port,@Selector boolean offline){
		String url = String.format("http://%s:%s", host, port);
		loadBalancerClient.maintain(serviceId, url, offline);
		return ApiResult.ok();
	}
	
}
