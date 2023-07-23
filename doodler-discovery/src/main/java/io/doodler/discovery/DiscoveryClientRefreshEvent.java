package io.doodler.discovery;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: DiscoveryClientRefreshEvent
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
public class DiscoveryClientRefreshEvent extends ApplicationEvent {

    private static final long serialVersionUID = 998914075734572211L;

    public DiscoveryClientRefreshEvent() {
        super(null);
    }
}