package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: InstanceStatusRefreshEvent
 * @Author: Fred Feng
 * @Date: 13/08/2024
 * @Version 1.0.0
 */
public class InstanceStatusRefreshEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4977108461302672813L;

    public InstanceStatusRefreshEvent(Object source, ServiceInstance serviceInstance) {
        super(source);
        this.serviceInstance = serviceInstance;
    }

    private final ServiceInstance serviceInstance;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

}
