package com.github.doodler.common.cloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 
 * @Description: DiscoveryClientProperties
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties("doodler.cloud")
public class DiscoveryClientProperties {

    private SiblingChecker siblingChecker;
    private ExclusiveChecker exclusiveChecker;

    @Data
    public static class SiblingChecker {

        private boolean quickStart = true;

    }

    @Data
    public static class ExclusiveChecker {

        private boolean quickStart = true;

    }

}
