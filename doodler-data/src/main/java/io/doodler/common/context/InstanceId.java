package io.doodler.common.context;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: InstanceId
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public final class InstanceId implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_PREFIX = "INS-";

    private String id = DEFAULT_PREFIX + UUID.randomUUID().toString();
    private final AtomicBoolean standby = new AtomicBoolean();

    public String get() {
        return id;
    }

    public boolean isStandby() {
        return standby.get();
    }
    
    public void setStandBy(boolean value) {
    	standby.set(value);
    }

    @EventListener({ApplicationReadyEvent.class})
    public void onApplicationReadyEvent() {
        standby.set(true);
        if(log.isInfoEnabled()) {
        	log.info("[{}] Web application is ready to accept connections ...", id);
        }
    }
}