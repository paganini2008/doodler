package com.github.doodler.common.cloud.eureka;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.IteratorUtils;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.DiscoveryClientService;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: EurekaDiscoveryClientService
 * @Author: Fred Feng
 * @Date: 01/05/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class EurekaDiscoveryClientService implements DiscoveryClientService {

    private final ApplicationInfoManager applicationInfoManager;

    @Override
    public Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
        return applicationInfoManager.getApplicationInfos(applicationName);
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getExclusiveApplicationInfos() {
        return applicationInfoManager.getApplicationInfos(false);
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getSiblingApplicationInfos() {
        Collection<ApplicationInfo> applicationInfos = applicationInfoManager.getSiblingApplicationInfos();
        Map<String, Collection<ApplicationInfo>> map = new ConcurrentHashMap<>();
        map.put(IteratorUtils.first(applicationInfos.iterator()).getServiceId(), applicationInfos);
        return map;
    }

}