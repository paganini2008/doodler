package com.github.doodler.common.feign.actuator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.feign.LoadBalancerClient;
import com.github.doodler.common.feign.RestClientMetadataCollector;
import com.github.doodler.common.feign.ServiceInstance;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: LoadBalancerClientEndpoint
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
@Endpoint(id = "loadBalancer")
public class LoadBalancerClientEndpoint {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestClientMetadataCollector restClientMetadataCollector;

    @ReadOperation
    public ApiResult<Map<String, Collection<ServiceInstance>>> candidates(@Selector Boolean filter) {
        if (filter != null && filter.booleanValue()) {
            Map<String, Collection<ServiceInstance>> candidates = loadBalancerClient.candidates();
            Set<String> serviceIds = restClientMetadataCollector.metadatas().stream().map(
                    info -> info.getServiceId()).collect(
                    Collectors.toSet());
            candidates = MapUtils.retainAll(candidates, serviceIds);
            return ApiResult.ok(candidates);
        }
        return ApiResult.ok(loadBalancerClient.candidates());
    }

    @WriteOperation
    public ApiResult<String> maintain(@Selector String serviceId,
                                      @Selector String url,
                                      @Selector Boolean offline) {
        loadBalancerClient.maintain(serviceId, url, offline);
        return ApiResult.ok();
    }

    @WriteOperation
    public ApiResult<String> maintain(@Selector String serviceId,
                                      @Selector String host,
                                      @Selector Integer port,
                                      @Selector Boolean offline) {
        String url = String.format("http://%s:%s", host, port);
        loadBalancerClient.maintain(serviceId, url, offline);
        return ApiResult.ok();
    }
}