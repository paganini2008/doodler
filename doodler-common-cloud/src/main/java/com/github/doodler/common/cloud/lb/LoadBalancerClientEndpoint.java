package com.github.doodler.common.cloud.lb;

import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cloud.ServiceInstance;

/**
 * 
 * @Description: LoadBalancerClientEndpoint
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@Endpoint(id = "loadBalancer")
public class LoadBalancerClientEndpoint {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @ReadOperation
    public ApiResult<Map<String, Collection<ServiceInstance>>> candidates() {
        return ApiResult.ok(loadBalancerClient.candidates());
    }

    @WriteOperation
    public ApiResult<String> maintain(@Selector String serviceId, @Selector String url,
            @Selector Boolean offline) {
        loadBalancerClient.maintain(serviceId, url, offline);
        return ApiResult.ok();
    }
}
