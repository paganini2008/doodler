package com.github.doodler.common.cloud.redis;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.ApplicationContextUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisRegistrationEndpoint
 * @Author: Fred Feng
 * @Date: 13/08/2024
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "redisRegistrations")
@RequiredArgsConstructor
public class RedisRegistrationEndpoint {

    private final Registration registration;
    private final ServiceInstanceManager serviceInstanceManager;

    @GetMapping("/")
    public ApiResult<Map<String, List<ServiceInstance>>> getApplicationInfos(@RequestParam(name = "serviceId", required = false) String serviceId) {
        if (StringUtils.isNotBlank(serviceId)) {
            return ApiResult.ok(
                    Collections.singletonMap(serviceId, serviceInstanceManager.getInstancesByServiceId(serviceId)));
        }

        return ApiResult.ok(serviceInstanceManager.getServices());
    }

    @PostMapping("/refresh")
    public ApiResult<String> forceRefresh() {
        ApplicationContextUtils.publishEvent(new InstanceStatusRefreshEvent(this, registration));
        return ApiResult.ok("Refreshing instances");
    }

}
