package com.github.doodler.common.cloud.redis;

import org.springframework.context.ApplicationListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: InstanceStatusChangeLogging
 * @Author: Fred Feng
 * @Date: 14/08/2024
 * @Version 1.0.0
 */
@Slf4j
public class InstanceStatusChangeLogging implements ApplicationListener<InstanceStatusChangeEvent> {

    @Override
    public void onApplicationEvent(InstanceStatusChangeEvent event) {
        if (log.isInfoEnabled()) {
            log.info("'{}' is '{}'", event.getServiceInstance(), event.getInstanceStatus());
        }
    }

}
