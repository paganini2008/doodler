package com.github.doodler.common.cloud;

import java.util.Collection;
import org.springframework.context.ApplicationEvent;

/**
 * @Description: SiblingApplicationInfoChangeEvent
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public class SiblingApplicationInfoChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4880922361101293113L;

    public SiblingApplicationInfoChangeEvent(Object source, Collection<AffectedApplicationInfo> affects) {
        super(source);
        this.affects = affects;
    }

    private final Collection<AffectedApplicationInfo> affects;

    public Collection<AffectedApplicationInfo> getAffects() {
        return affects;
    }
}