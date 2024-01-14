package com.github.doodler.common.discovery;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @Description: SiblingApplicationInfoHeartbeatChecker
 * @Author: Fred Feng
 * @Date: 02/09/2023
 * @Version 1.0.0
 */
public class SiblingApplicationInfoHeartbeatChecker extends HeartbeatChecker {

    private final ApplicationInfoManager applicationInfoManager;

    public SiblingApplicationInfoHeartbeatChecker(long initialDelay, long checkInterval, Heartbeater heartbeater,
                                                  ApplicationInfoManager applicationInfoManager) {
        super(initialDelay, checkInterval, heartbeater);
        this.applicationInfoManager = applicationInfoManager;
    }

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
        map.put(IteratorUtils.first(applicationInfos.iterator()).getApplicationName(), applicationInfos);
        return map;
    }

    @Override
    protected void handleAffectedApplicationInfos(Collection<AffectedApplicationInfo> affects,
                                                  ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new SiblingApplicationInfoChangeEvent(this, affects));
    }
}