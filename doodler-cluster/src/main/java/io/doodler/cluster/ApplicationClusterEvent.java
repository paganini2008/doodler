package io.doodler.cluster;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: ApplicationClusterEvent
 * @Author: Fred Feng
 * @Date: 22/07/2023
 * @Version 1.0.0
 */
public abstract class ApplicationClusterEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public ApplicationClusterEvent(Object source) {
        super(source);
    }
}