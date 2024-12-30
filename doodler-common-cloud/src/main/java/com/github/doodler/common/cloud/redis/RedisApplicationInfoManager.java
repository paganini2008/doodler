package com.github.doodler.common.cloud.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.SiblingApplicationCondition;
import com.github.doodler.common.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisApplicationInfoManager implements ApplicationInfoManager {

    private final String serviceName;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final ServiceInstanceManager serviceInstanceManager;
    private final SiblingApplicationCondition siblingApplicationCondition;

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includeSelf) {
        Map<String, List<ServiceInstance>> instances = serviceInstanceManager.getServices();
        Map<String, Collection<ApplicationInfo>> appInfoMap = new HashMap<>();
        instances.entrySet().forEach(e -> {
            if (includeSelf || (!includeSelf && !e.getKey().equalsIgnoreCase(serviceName))) {
                appInfoMap.put(e.getKey(),
                        BeanCopyUtils.copyBeanList(e.getValue(), ApplicationInfo.class));
            }
        });
        return appInfoMap;
    }

    @Override
    public Collection<ApplicationInfo> getSiblingApplicationInfos() {
        Collection<ApplicationInfo> instanceInfos = getApplicationInfos(serviceName);
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return Collections.emptyList();
        }
        return instanceInfos.stream()
                .filter(info -> siblingApplicationCondition
                        .isSiblingApplication(applicationInfoHolder.get(), info))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMetadata(Map<String, String> data) {
        org.springframework.cloud.client.ServiceInstance serviceInstance =
                BeanCopyUtils.copyBean(applicationInfoHolder.get(), ApplicationInstance.class);
        serviceInstanceManager.updateMetadata(serviceInstance, data);
    }

    @Override
    public Map<String, String> getMetadata() {
        org.springframework.cloud.client.ServiceInstance serviceInstance =
                BeanCopyUtils.copyBean(applicationInfoHolder.get(), ApplicationInstance.class);
        return serviceInstanceManager.getMetadata(serviceInstance);
    }

}
