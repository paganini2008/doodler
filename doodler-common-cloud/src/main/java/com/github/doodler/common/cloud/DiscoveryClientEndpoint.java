package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.ApplicationContextUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: DiscoveryClientEndpoint
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "discoveryClient")
@RequiredArgsConstructor
public class DiscoveryClientEndpoint {

    private final DiscoveryClientService discoveryClientService;

    @GetMapping("/")
    public ApiResult<Map<String, Collection<ApplicationInfo>>> getApplicationInfos(@RequestParam(name = "applicationName", required = false) String applicationName) {
        if (StringUtils.isNotBlank(applicationName)) {
            return ApiResult.ok(
                    Collections.singletonMap(applicationName, discoveryClientService.getApplicationInfos(applicationName)));
        }
        return ApiResult.ok(discoveryClientService.getExclusiveApplicationInfos());
    }

    @PostMapping("/refresh")
    public ApiResult<String> forceRefresh() {
        ApplicationContextUtils.publishEvent(new ApplicationInfoRefreshEvent(this));
        return ApiResult.ok("Operate successfully");
    }
}