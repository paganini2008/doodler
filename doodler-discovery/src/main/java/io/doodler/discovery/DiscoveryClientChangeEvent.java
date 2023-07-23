package io.doodler.discovery;

import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 * @Description: DiscoveryClientChangeEvent
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public class DiscoveryClientChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1125948574959171282L;

    public DiscoveryClientChangeEvent(Object source, List<AffectedApplicationInfo> affects) {
        super(source);
        this.affects = affects;
    }

    private final List<AffectedApplicationInfo> affects;

    public List<AffectedApplicationInfo> getAffects() {
        return affects;
    }
}