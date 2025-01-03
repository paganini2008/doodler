package com.github.doodler.common.zk.election;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: LeaderElectionProperties
 * @Author: Fred Feng
 * @Date: 02/08/2024
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("spring.application.zookeeper")
public class LeaderElectionProperties {

    private String zkNodes = "localhost:2181";
    private String path = "/zk_leader_election";
    private int sessionTimeout = 60000;
    private int connectionTimeout = 10000;
    private int retryCount = 10;
    private int rulePeriod = -1;

}
