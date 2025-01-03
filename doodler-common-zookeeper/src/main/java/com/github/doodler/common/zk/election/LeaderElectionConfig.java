package com.github.doodler.common.zk.election;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.context.InstanceId;

/**
 * 
 * @Description: LeaderElectionConfig
 * @Author: Fred Feng
 * @Date: 02/08/2024
 * @Version 1.0.0
 */
@EnableConfigurationProperties({LeaderElectionProperties.class})
@Configuration(proxyBeanMethods = false)
public class LeaderElectionConfig {

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework zkClient(LeaderElectionProperties electionProperties) {
        return CuratorFrameworkFactory.newClient(electionProperties.getZkNodes(), electionProperties.getSessionTimeout(),
                electionProperties.getConnectionTimeout(), new ExponentialBackoffRetry(1000,
                        electionProperties.getRetryCount()));
    }

    @Bean
    public LeaderElectionContext leaderSelector(InstanceId instanceId,
                                          CuratorFramework zkClient,
                                          LeaderElectionProperties electionProperties,
                                          List<LeaderElectionListener> electionListeners) {
        return new LeaderElectionContext(instanceId, zkClient, electionProperties, electionListeners);
    }

}
