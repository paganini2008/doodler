package com.github.doodler.common.cloud.zookeeper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.DiscoveryClientRegistrar;
import com.github.doodler.common.cloud.MetadataCollector;
import com.github.doodler.common.cloud.SiblingApplicationCondition;

/**
 * 
 * @Description: ZookeeperDiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 09/09/2024
 * @Version 1.0.0
 */
@ConditionalOnClass({ZookeeperDiscoveryClient.class})
@AutoConfigureBefore(ZookeeperServiceRegistryAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class ZookeeperDiscoveryClientConfig {

    @Autowired
    public void configureApplicationInfo(ApplicationInfoHolder applicationInfoHolder,
            ZookeeperDiscoveryProperties config, List<MetadataCollector> metadataCollectors) {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        config.setInstanceId(applicationInfo.getInstanceId());
        if (metadataCollectors != null) {
            Map<String, String> mergedMap = metadataCollectors.stream()
                    .map(MetadataCollector::getInitialData).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (existing, replacement) -> replacement));
            config.getMetadata().putAll(mergedMap);
        }
    }

    @Bean
    public ApplicationInfoManager applicationInfoManager(
            ZookeeperDiscoveryClient zookeeperDiscoveryClient,
            ApplicationInfoHolder applicationInfoHolder,
            SiblingApplicationCondition siblingApplicationCondition) {
        return new ZookeeperApplicationInfoManager(zookeeperDiscoveryClient, applicationInfoHolder,
                siblingApplicationCondition);
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientRegistrar zookeeperDiscoveryClientRegistrar() {
        return new ZookeeperDiscoveryClientRegistrar();
    }

}
