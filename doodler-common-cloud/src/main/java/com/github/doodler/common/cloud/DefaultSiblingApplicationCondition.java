package com.github.doodler.common.cloud;

import org.springframework.cloud.client.ServiceInstance;

/**
 * 
 * @Description: DefaultSiblingApplicationCondition
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public class DefaultSiblingApplicationCondition implements SiblingApplicationCondition {

    @Override
    public boolean isSiblingApplication(ServiceInstance self, ServiceInstance other) {
        return other.getServiceId().equals(self.getServiceId())
                && (!other.getInstanceId().equals(self.getInstanceId())
                        || !other.getHost().equals(self.getHost())
                        || other.getPort() != self.getPort());
    }

    @Override
    public boolean isSiblingApplication(ApplicationInfo self, ApplicationInfo other) {
        return other.getServiceId().equals(self.getServiceId())
                && (!other.getInstanceId().equals(self.getInstanceId())
                        || !other.getHost().equals(self.getHost())
                        || other.getPort() != self.getPort());
    }



}
