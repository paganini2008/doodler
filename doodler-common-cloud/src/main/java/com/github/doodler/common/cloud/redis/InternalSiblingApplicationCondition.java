package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import com.github.doodler.common.cloud.ClusterSiblingApplicationCondition;

/**
 * 
 * @Description: InternalSiblingApplicationCondition
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public class InternalSiblingApplicationCondition extends ClusterSiblingApplicationCondition {

    @Override
    public boolean isSiblingApplication(ServiceInstance self, ServiceInstance other) {
        return ((ApplicationInstance) other).getClusterId()
                .equals(((ApplicationInstance) self).getClusterId())
                && (!other.getInstanceId().equals(self.getInstanceId())
                        || !other.getHost().equals(self.getHost())
                        || other.getPort() != self.getPort());
    }
}
