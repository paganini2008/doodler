package com.github.doodler.common.cloud;

/**
 * 
 * @Description: ClusterSiblingApplicationCondition
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public class ClusterSiblingApplicationCondition implements SiblingApplicationCondition {

    @Override
    public boolean isSiblingApplication(ApplicationInfo self, ApplicationInfo other) {
        return other.getClusterId().equals(self.getClusterId())
                && (!other.getInstanceId().equals(self.getInstanceId())
                        || !other.getHost().equals(self.getHost())
                        || other.getPort() != self.getPort());
    }
}
