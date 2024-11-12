package com.github.doodler.common.scheduling;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.election.LeaderElectionConfig;
import com.github.doodler.common.election.LeaderElectionContext;

/**
 * 
 * @Description: MultiSchedulingConfig
 * @Author: Fred Feng
 * @Date: 14/08/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({LeaderElectionConfig.class})
public class MultiSchedulingConfig {

    @Bean
    public MultiSchedulingCrossCutting multiSchedulingCrossCutting(LeaderElectionContext leaderElectionContext) {
        return new MultiSchedulingCrossCutting(leaderElectionContext);
    }

}
