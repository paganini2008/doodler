package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: InstanceStatusChangeEvent
 * @Author: Fred Feng
 * @Date: 11/08/2024
 * @Version 1.0.0
 */
public class InstanceStatusChangeEvent extends ApplicationEvent {

    public InstanceStatusChangeEvent(ServiceInstance serviceInstance, InstanceStatus instanceStatus) {
        super(serviceInstance);
        this.serviceInstance = serviceInstance;
        this.instanceStatus = instanceStatus;
    }

    private static final long serialVersionUID = 3588438045910340561L;

    private final ServiceInstance serviceInstance;

    private final InstanceStatus instanceStatus;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

}
