package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisServiceRegistry
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisServiceRegistry implements ServiceRegistry<Registration>,
        ApplicationListener<InstanceStatusRefreshEvent> {

    private final ServiceInstanceManager serviceInstanceManager;

    @Override
    public void register(Registration registration) {
        ApplicationInstance applicationInstance = ApplicationInstance.fromRegistration(registration);
        if (!applicationInstance.isRegisterSelf()) {
            return;
        }
        serviceInstanceManager.registerInstance(applicationInstance);
        serviceInstanceManager.cleanExpiredInstances(applicationInstance);
    }

    @Override
    public void deregister(Registration registration) {
        serviceInstanceManager.deregisterInstance(ApplicationInstance.fromRegistration(registration));
    }

    @Override
    public void close() {
    }

    @Override
    public void setStatus(Registration registration, String status) {
        ApplicationInstance instance = ApplicationInstance.fromRegistration(registration);
        serviceInstanceManager.setInstanceStatus(instance, status);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getStatus(Registration registration) {
        ServiceInstance instance = serviceInstanceManager.getInstanceByServiceIdAndHost(registration.getServiceId(),
                registration.getHost(), registration.getPort());
        return serviceInstanceManager.getInstanceStatus(instance);
    }

    @Override
    public void onApplicationEvent(InstanceStatusRefreshEvent event) {
        ApplicationInstance applicationInstance = ApplicationInstance.fromRegistration(
                (Registration) event.getServiceInstance());
        if (!applicationInstance.isRegisterSelf()) {
            return;
        }
        serviceInstanceManager.registerInstance(applicationInstance);
        serviceInstanceManager.cleanExpiredInstances(applicationInstance);
    }

}
