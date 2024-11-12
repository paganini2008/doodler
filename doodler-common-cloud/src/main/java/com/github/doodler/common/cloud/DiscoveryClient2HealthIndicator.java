package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DiscoveryClient2HealthIndicator
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DiscoveryClient2HealthIndicator extends AbstractHealthIndicator {

    private final DiscoveryClientService discoveryClientService;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        Map<String, Collection<ApplicationInfo>> map = discoveryClientService.getExclusiveApplicationInfos();
        if (MapUtils.isNotEmpty(map)) {
            if (map.entrySet().stream().anyMatch(e -> e.getValue().isEmpty())) {
                builder.down();
            } else {
                builder.up();
            }
        } else {
            builder.unknown();
        }
        for (Map.Entry<String, Collection<ApplicationInfo>> entry : map.entrySet()) {
            builder.withDetail(entry.getKey(), entry.getValue().size());
        }
        builder.build();
    }
}