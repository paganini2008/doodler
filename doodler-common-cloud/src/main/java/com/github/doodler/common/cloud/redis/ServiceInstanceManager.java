package com.github.doodler.common.cloud.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.ServiceInstance;

/**
 * 
 * @Description: ServiceInstanceManager
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
public interface ServiceInstanceManager {

    List<ServiceInstance> getInstancesByServiceId(String serviceId);

    ServiceInstance getInstanceByServiceIdAndHost(String serviceId, String host, int port);

    Collection<String> getServiceNames();

    Map<String, List<ServiceInstance>> getServices();

    String getInstanceStatus(ServiceInstance instance);

    void setInstanceStatus(ServiceInstance instance, String status);

    void registerInstance(ServiceInstance instance);

    void deregisterInstance(ServiceInstance instance);

    void updateMetadata(ServiceInstance instance, Map<String, String> metadata);

    void updateInstance(ServiceInstance instance);

    void cleanExpiredInstances(ServiceInstance instance);

}