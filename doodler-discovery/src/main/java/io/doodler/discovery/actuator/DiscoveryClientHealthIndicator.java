package io.doodler.discovery.actuator;

import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import io.doodler.discovery.ApplicationInfo;
import io.doodler.discovery.DiscoveryClientService;

/**
 * @Description: DiscoveryClientHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DiscoveryClientHealthIndicator extends AbstractHealthIndicator {

    private final DiscoveryClientService discoveryClientService;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        builder.up();
        Map<String, Collection<ApplicationInfo>> map = discoveryClientService.getApplicationInfos();
        for (Map.Entry<String, Collection<ApplicationInfo>> entry : map.entrySet()) {
            builder.withDetail(entry.getKey(), entry.getValue().size());
        }
        builder.build();
    }
}