package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 
 * @Description: SiblingDiscoveryClientChecker
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
public class SiblingDiscoveryClientChecker extends DiscoveryClientChecker {

    public SiblingDiscoveryClientChecker(long initialDelay, long checkInterval,
                                         ApplicationInfoManager applicationInfoManager) {
        super(initialDelay, checkInterval);
        this.applicationInfoManager = applicationInfoManager;
    }

    private final ApplicationInfoManager applicationInfoManager;

    @Override
    protected Map<String, Collection<ApplicationInfo>> initialize() {
        return Collections.emptyMap();
    }

    @Override
    protected Map<String, Collection<ApplicationInfo>> fetchApplicationInfos() {
        Collection<ApplicationInfo> applicationInfos = applicationInfoManager.getSiblingApplicationInfos();
        if (CollectionUtils.isEmpty(applicationInfos)) {
            return Collections.emptyMap();
        }
        Map<String, Collection<ApplicationInfo>> map = new ConcurrentHashMap<>();
        map.put(IteratorUtils.first(applicationInfos.iterator()).getServiceId(), applicationInfos);
        return map;
    }

    @Override
    protected void handleAffectedApplicationInfos(Collection<AffectedApplicationInfo> affects,
                                                  ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new SiblingApplicationInfoChangeEvent(this, affects));
    }

}
