package io.doodler.common.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.multimap.MultiMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HazelcastApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HazelcastApplicationInfoManager implements ApplicationInfoManager {

    private static final String DEFAULT_CACHE_NAME_PATTERN = "%s:service:instances";

    private final HazelcastInstance hazelcastInstance;

    private final ApplicationInfoHolder applicationInfoHolder;

    @Value("${spring.application.cluster.name:default}")
    private String clusterName;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public ApplicationInfo registerSelf() {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        MultiMap<String, ApplicationInfo> mmap = hazelcastInstance.getMultiMap(
                String.format(DEFAULT_CACHE_NAME_PATTERN, clusterName));
        mmap.put(applicationInfo.getApplicationName(), applicationInfo);
        if (log.isInfoEnabled()) {
            log.info("Add application instance: '{}' to '{}'", applicationInfo, applicationInfo.getApplicationName());
        }
        return applicationInfo;
    }

    @Override
    public List<ApplicationInfo> cleanExpiredSiblings(Heartbeater heartbeater) {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        final MultiMap<String, ApplicationInfo> mmap = hazelcastInstance.getMultiMap(
                String.format(DEFAULT_CACHE_NAME_PATTERN, clusterName));
        Collection<ApplicationInfo> instances = mmap.get(applicationInfo.getApplicationName());
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        List<ApplicationInfo> expiredList = new ArrayList<>();
        instances.stream()
                .filter(info -> applicationInfo.hasSibling(info)).forEach(info -> {
                    if (!heartbeater.isAlive(info)) {
                        mmap.get(info.getApplicationName()).remove(info);
                        if (log.isInfoEnabled()) {
                            log.info("Remove expired application '{}' from '{}'", info, info.getApplicationName());
                        }
                        expiredList.add(info);
                    }
                });
        return expiredList;
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf) {
        Map<String, Collection<ApplicationInfo>> resultMap = new ConcurrentHashMap<>();
        MultiMap<String, ApplicationInfo> mmap = hazelcastInstance.getMultiMap(
                String.format(DEFAULT_CACHE_NAME_PATTERN, clusterName));
        for (String key : mmap.keySet()) {
            if (includedSelf || !key.equals(applicationName)) {
                resultMap.put(key, new CopyOnWriteArrayList<>(mmap.get(key)));
            }
        }
        return resultMap;
    }

    @Override
    public Collection<ApplicationInfo> getSiblingApplicationInfos() {
        MultiMap<String, ApplicationInfo> mmap = hazelcastInstance.getMultiMap(
                String.format(DEFAULT_CACHE_NAME_PATTERN, clusterName));
        Collection<ApplicationInfo> values = mmap.get(applicationName);
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .filter(info -> applicationInfoHolder.get().hasSibling(info)).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }
}