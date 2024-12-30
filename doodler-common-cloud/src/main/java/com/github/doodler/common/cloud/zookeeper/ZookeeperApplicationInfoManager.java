package com.github.doodler.common.cloud.zookeeper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.SiblingApplicationCondition;
import com.github.doodler.common.cloud.redis.CloudConstants;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: ZookeeperApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 09/09/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ZookeeperApplicationInfoManager implements ApplicationInfoManager {

    private final ZookeeperDiscoveryClient zookeeperDiscoveryClient;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final SiblingApplicationCondition siblingApplicationCondition;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf) {
        List<String> serviceIds = zookeeperDiscoveryClient.getServices();
        if (CollectionUtils.isEmpty(serviceIds)) {
            return Collections.emptyMap();
        }
        Map<String, Collection<ApplicationInfo>> results = new HashMap<>();
        for (String serviceId : serviceIds) {
            List<ServiceInstance> serviceInstances =
                    zookeeperDiscoveryClient.getInstances(serviceId);
            if (includedSelf || (!includedSelf && !serviceId.equalsIgnoreCase(applicationName))) {
                results.put(serviceId, serviceInstances.stream().map(this::convert2ApplicationInfo)
                        .filter(i -> i != null).collect(Collectors.toList()));
            }
        }
        return results;
    }

    private ApplicationInfo convert2ApplicationInfo(ServiceInstance serviceInstance) {
        String appInfoString =
                serviceInstance.getMetadata().get(CloudConstants.METADATA_APPLICATION_INFO);
        if (StringUtils.isBlank(appInfoString)) {
            return null;
        }
        Map<String, String> copy = new HashMap<>(serviceInstance.getMetadata());
        copy.remove(CloudConstants.METADATA_APPLICATION_INFO);
        appInfoString = new String(Base64.decodeBase64(appInfoString));
        ApplicationInfo applicationInfo =
                JacksonUtils.parseJson(appInfoString, ApplicationInfo.class);
        applicationInfo.setMetadata(copy);
        return applicationInfo;
    }

    @Override
    public Collection<ApplicationInfo> getSiblingApplicationInfos() {
        Collection<ApplicationInfo> instanceInfos = getApplicationInfos(applicationName);
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return Collections.emptyList();
        }
        return instanceInfos.stream()
                .filter(info -> siblingApplicationCondition
                        .isSiblingApplication(applicationInfoHolder.get(), info))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getMetadata() {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        List<ServiceInstance> serviceInstances =
                zookeeperDiscoveryClient.getInstances(applicationInfo.getServiceId());
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return Collections.emptyMap();
        }
        Optional<ServiceInstance> opt = serviceInstances.stream()
                .filter(i -> i.getInstanceId().equals(applicationInfo.getInstanceId())).findFirst();
        if (opt.isPresent()) {
            return opt.get().getMetadata();
        }
        return Collections.emptyMap();
    }

}
