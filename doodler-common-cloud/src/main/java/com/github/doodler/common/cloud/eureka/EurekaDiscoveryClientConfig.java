package com.github.doodler.common.cloud.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.DiscoveryClientChecker;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.cloud.SiblingDiscoveryClientChecker;
import com.github.doodler.common.context.InstanceId;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;

/**
 * 
 * @Description: CloudDiscoveryClientConfig
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
@ConditionalOnClass({EurekaClient.class, DiscoveryClient.class})
@Configuration(proxyBeanMethods = false)
public class EurekaDiscoveryClientConfig {

    @Bean
    public ApplicationInfoManager applicationInfoManager(EurekaClient eurekaClient,
                                                         com.netflix.appinfo.ApplicationInfoManager appInfoManager,
                                                         ApplicationInfoHolder applicationInfoHolder) {
        return new EurekaApplicationInfoManager(eurekaClient, appInfoManager, applicationInfoHolder);
    }

    @ConditionalOnMissingBean
    @Bean
    public DiscoveryClientService eurekaDiscoveryClientService(ApplicationInfoManager applicationInfoManager) {
        return new EurekaDiscoveryClientService(applicationInfoManager);
    }

    @Bean
    public EurekaDiscoveryClientRegistrar eurekaDiscoveryClientRegistrar() {
        return new EurekaDiscoveryClientRegistrar();
    }

    @Bean("eurekaSiblingDiscoveryClientChecker")
    public DiscoveryClientChecker eurekaSiblingDiscoveryClientChecker(ApplicationInfoManager applicationInfoManager) {
        return new SiblingDiscoveryClientChecker(60, 30, applicationInfoManager);
    }

    @Autowired
    public void setInstanceId(InstanceId instanceId,
                              EurekaInstanceConfigBean configBean) {
        configBean.setInstanceId(instanceId.get());
    }

}