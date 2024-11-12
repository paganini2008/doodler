package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;

/**
 * 
 * @Description: ExclusiveDiscoveryClientChecker
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
public class ExclusiveDiscoveryClientChecker extends DiscoveryClientChecker {

    private final ApplicationInfoManager applicationInfoManager;

    public ExclusiveDiscoveryClientChecker(long initialDelay,
                                           long checkInterval,
                                           ApplicationInfoManager applicationInfoManager) {
        super(initialDelay, checkInterval);
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