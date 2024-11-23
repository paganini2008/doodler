package com.github.doodler.common.webmvc.actuator;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.doodler.common.utils.TimeWindowMap;
import com.github.doodler.common.utils.TimeWindowUnit;

/**
 * @Description: MemoryUsageHealthIndicator
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "management.health.memoryUsage.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class MemoryUsageHealthIndicator extends AbstractHealthIndicator {

    private final TimeWindowMap<AtomicInteger> counter = new TimeWindowMap<>(5, TimeWindowUnit.MINUTES,
            TimeZone.getDefault(), 60);

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        double usageRate = (double) (totalMemory - freeMemory) / maxMemory;
        AtomicInteger counter = getCounter();
        if (usageRate > 0.8d) {
            int n = counter.incrementAndGet();
            if (n >= 5) {
                counter.set(0);
                builder.down();
            } else {
                builder.up();
            }
        } else {
            if (counter.get() > 0) {
                counter.decrementAndGet();
            }
            builder.up();
        }
        builder.withDetail("maxMemory", FileUtils.byteCountToDisplaySize(maxMemory));
        builder.withDetail("totalMemory", FileUtils.byteCountToDisplaySize(totalMemory));
        builder.withDetail("usage", new DecimalFormat("#.0%").format(usageRate));

        builder.build();
    }

    private AtomicInteger getCounter() {
        Instant now = Instant.now();
        AtomicInteger ai = counter.get(now);
        if (ai == null) {
            counter.putIfAbsent(now, new AtomicInteger());
            ai = counter.get(now);
        }
        return ai;
    }
}