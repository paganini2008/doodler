package io.doodler.discovery;

import java.util.Collection;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @Description: DiscoveryClientHeartbeatChecker
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public class DiscoveryClientHeartbeatChecker extends HeartbeatChecker {

    private final ApplicationInfoManager applicationInfoManager;

    public DiscoveryClientHeartbeatChecker(long initialDelay, long checkInterval, Heartbeater heartbeater,
                                           ApplicationInfoManager applicationInfoManager) {
        super(initialDelay, checkInterval, heartbeater);
        this.applicationInfoManager = applicationInfoManager;
    }

    @Override
    protected Map<String, Collection<ApplicationInfo>> initialize() {
        return fetchApplicationInfos();
    }

    @Override
    protected Map<String, Collection<ApplicationInfo>> fetchApplicationInfos() {
        return applicationInfoManager.getApplicationInfos(false);
    }

    @Override
    protected void handleAffectedApplicationInfos(Collection<AffectedApplicationInfo> affects,
                                                  ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new DiscoveryClientChangeEvent(this, affects));
    }
}