package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * 
 * @Description: RedisAutoServiceRegistry
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
public class RedisAutoServiceRegistry extends AbstractAutoServiceRegistration<Registration> {

    private ServiceRegistration serviceRegistration;
    private ServiceRegistration managementServiceRegistration;

    public RedisAutoServiceRegistry(ServiceRegistry<Registration> serviceRegistry,
                                    AutoServiceRegistrationProperties properties,
                                    ServiceRegistration serviceRegistration,
                                    ServiceRegistration managementServiceRegistration) {
        super(serviceRegistry, properties);
        this.serviceRegistration = serviceRegistration;
        this.managementServiceRegistration = managementServiceRegistration;
    }

    @Override
    protected void register() {
        super.register();
    }

    @Override
    protected Object getConfiguration() {
        return this.serviceRegistration.getConfiguration();
    }

    @Override
    protected boolean isEnabled() {
        return serviceRegistration.isEnabled();
    }

    @Override
    protected Registration getRegistration() {
        return serviceRegistration;
    }

    @Override
    protected Registration getManagementRegistration() {
        return managementServiceRegistration;
    }
}
