package com.github.doodler.common.cloud.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientRegistrar;
import lombok.Setter;

/**
 * 
 * @Description: RedisDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 11/08/2024
 * @Version 1.0.0
 */
public class RedisDiscoveryClientRegistrar implements DiscoveryClientRegistrar {

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationInfoHolder applicationInfoHolder;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this, applicationInfoHolder.get()));
    }

}
