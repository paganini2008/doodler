package com.github.doodler.common.discovery.actuator;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.github.doodler.common.discovery.ApplicationInfo;
import com.github.doodler.common.discovery.DiscoveryClientService;

import lombok.RequiredArgsConstructor;

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
        Map<String, Collection<ApplicationInfo>> map = discoveryClientService.getApplicationInfos();
        if(MapUtils.isNotEmpty(map)) {
        	builder.up();
        }else {
        	builder.unknown();
        }
        for (Map.Entry<String, Collection<ApplicationInfo>> entry : map.entrySet()) {
            builder.withDetail(entry.getKey(), entry.getValue().size());
        }
        builder.build();
    }
}