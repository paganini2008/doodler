package com.github.doodler.common.zk.scheduling;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.zk.election.LeaderElectionConfig;
import com.github.doodler.common.zk.election.LeaderElectionContext;

/**
 * 
 * @Description: CrosscuttingSchedulingConfig
 * @Author: Fred Feng
 * @Date: 14/08/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({LeaderElectionConfig.class})
public class CrosscuttingSchedulingConfig {

    @Bean
    public CrosscuttingSchedulingTasks multiSchedulingCrossCutting(
            LeaderElectionContext leaderElectionContext) {
        return new CrosscuttingSchedulingTasks(leaderElectionContext);
    }

}
