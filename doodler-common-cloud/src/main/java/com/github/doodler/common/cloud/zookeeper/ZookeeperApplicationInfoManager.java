package com.github.doodler.common.cloud.zookeeper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                results.put(serviceId, serviceInstances.stream().map(this::convert)
                        .filter(i -> i != null).collect(Collectors.toList()));
            }
        }
        return results;
    }

    private ApplicationInfo convert(ServiceInstance serviceInstance) {
        String appInfoString =
                serviceInstance.getMetadata().get(CloudConstants.METADATA_APPLICATION_INFO);
        if (StringUtils.isBlank(appInfoString)) {
            return null;
        }
        appInfoString = new String(Base64.decodeBase64(appInfoString));
        return JacksonUtils.parseJson(appInfoString, ApplicationInfo.class);
    }

    @Override
    public Collection<ApplicationInfo> getSiblingApplicationInfos() {
        Collection<ApplicationInfo> instanceInfos = getApplicationInfos(applicationName);
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return Collections.emptyList();
        }
        return instanceInfos.stream().filter(info -> applicationInfoHolder.get().isSibling(info))
                .collect(Collectors.toList());
    }

}
