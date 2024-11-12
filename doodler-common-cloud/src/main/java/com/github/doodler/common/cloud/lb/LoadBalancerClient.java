package com.github.doodler.common.cloud.lb;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;
import com.github.doodler.common.cloud.ServiceInstance;

/**
 * 
 * @Description: LoadBalancerClient
 * @Author: Fred Feng
 * @Date: 13/08/2024
 * @Version 1.0.0
 */
public interface LoadBalancerClient {

    Map<String, Collection<ServiceInstance>> candidates();

    boolean contains(String serviceId);

    ServiceInstance chooseFirst(String serviceId);

    ServiceInstance choose(String serviceId, @Nullable Object requestProxy);

    @Nullable
    ServiceInstance currentSelected(String serviceId);

    URI reconstructURI(ServiceInstance instance, URI originalUri);

    void maintain(String serviceId, String url, boolean online);
}