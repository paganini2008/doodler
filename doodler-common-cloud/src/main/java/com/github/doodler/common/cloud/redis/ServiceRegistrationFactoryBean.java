package com.github.doodler.common.cloud.redis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.MetadataCollector;
import com.github.doodler.common.context.ContextPath;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: ServiceRegistrationFactoryBean
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ServiceRegistrationFactoryBean implements FactoryBean<ServiceRegistration> {

    @Autowired
    private ApplicationInfoHolder applicationInfoHolder;

    @Autowired
    private List<MetadataCollector> metadataCollectors;

    @Value("${spring.application.weight:1}")
    private int weight;

    @Value("${spring.cloud.redis.discovery.registerSelf:true}")
    private boolean registerSelf;

    @Autowired
    private ContextPath contextPath;

    @Override
    public ServiceRegistration getObject() throws Exception {
        final ApplicationInfo applicationInfo = applicationInfoHolder.get();
        RedisRegistration redisRegistration = new RedisRegistration();
        redisRegistration.setClusterId(applicationInfo.getClusterId());
        redisRegistration.setApplicationName(applicationInfo.getApplicationName());
        redisRegistration.setInstanceId(applicationInfo.getInstanceId());
        redisRegistration.setHost(applicationInfo.getHost());
        redisRegistration.setPort(applicationInfo.getPort());
        redisRegistration.setSecure(applicationInfo.isSecure());
        redisRegistration.setExternalHost(applicationInfo.getExternalHost());
        redisRegistration.setContextPath(contextPath.getContextPath());
        redisRegistration.setActuatorContextPath(applicationInfo.getActuatorContextPath());
        redisRegistration.setActuatorPort(applicationInfo.getActuatorPort());
        redisRegistration.setWeight(weight);
        redisRegistration.setRegisterSelf(registerSelf);
        if (metadataCollectors != null) {
            Map<String, String> mergedMap = metadataCollectors.stream()
                    .map(MetadataCollector::getInitialData).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (existing, replacement) -> replacement));
            redisRegistration.getMetadata().putAll(mergedMap);
        }
        return redisRegistration;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisRegistration.class;
    }

}
