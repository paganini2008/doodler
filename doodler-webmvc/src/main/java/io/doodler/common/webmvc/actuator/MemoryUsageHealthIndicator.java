package io.doodler.common.webmvc.actuator;

import java.text.DecimalFormat;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Description: MemoryUsageHealthIndicator
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "management.health.memoryUsage.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class MemoryUsageHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        double usageRate = (double)(totalMemory - freeMemory) / maxMemory;
        if (usageRate > 0.8d) {
            builder.down();
        } else {
            builder.up();
        }
        builder.withDetail("maxMemory", FileUtils.byteCountToDisplaySize(maxMemory));
        builder.withDetail("totalMemory", FileUtils.byteCountToDisplaySize(totalMemory));
        builder.withDetail("usage", new DecimalFormat("#.0%").format(usageRate));

        builder.build();
    }
}