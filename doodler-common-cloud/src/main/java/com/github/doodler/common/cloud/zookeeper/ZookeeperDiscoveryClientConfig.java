package com.github.doodler.common.cloud.zookeeper;

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
import com.github.doodler.common.cloud.redis.CloudConstants;
import com.github.doodler.common.context.ENC;
import com.github.doodler.common.utils.JacksonUtils;

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
            ZookeeperDiscoveryProperties config) {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        config.setInstanceId(applicationInfo.getInstanceId());
        String appInfoStr = JacksonUtils.toJsonString(applicationInfo);
        appInfoStr = ENC.encrypt(appInfoStr);
        config.getMetadata().put(CloudConstants.METADATA_APPLICATION_INFO, appInfoStr);
    }

    @Bean
    public ApplicationInfoManager applicationInfoManager(
            ZookeeperDiscoveryClient zookeeperDiscoveryClient,
            ApplicationInfoHolder applicationInfoHolder) {
        return new ZookeeperApplicationInfoManager(zookeeperDiscoveryClient, applicationInfoHolder);
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientRegistrar zookeeperDiscoveryClientRegistrar() {
        return new ZookeeperDiscoveryClientRegistrar();
    }

}
