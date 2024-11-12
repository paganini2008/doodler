package com.github.doodler.common.cloud;

import java.util.Collection;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: DiscoveryClientChangeEvent
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public class DiscoveryClientChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1125948574959171282L;

    public DiscoveryClientChangeEvent(Object source, Collection<AffectedApplicationInfo> affects) {
        super(source);
        this.affects = affects;
    }

    private final Collection<AffectedApplicationInfo> affects;

    public Collection<AffectedApplicationInfo> getAffects() {
        return affects;
    }
}