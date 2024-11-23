package com.github.doodler.common.webflux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.Constants;
import com.github.doodler.common.ThrowableInfo;
import com.github.doodler.common.context.InstanceId;

import reactor.core.publisher.Mono;

/**
 * 
 * @Description: WebFluxEndpoint
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@RestController
public class WebFluxEndpoint {

    @Autowired
    private ApiExceptionContext exceptionContext;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private InstanceId instanceId;

    @GetMapping("/")
    public ApiResult<Map<String, String>> index() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("project", Constants.PROJECT_NAME);
        info.put("version", Constants.VERSION);
        info.put("name", applicationName);
        return ApiResult.ok(info);
    }

    @GetMapping("/ping")
    public Mono<ApiResult<String>> ping() {
        return Mono.just(ApiResult.ok(instanceId.isStandby() ? "UP" : "DOWN"));
    }

    @GetMapping("/latest/throwables")
    public ApiResult<List<ThrowableInfo>> getLatestThrowables() {
        List<ThrowableInfo> list = new ArrayList<>(exceptionContext.getExceptionTraces());
        Collections.sort(list);
        return ApiResult.ok(list);
    }

    @GetMapping("/favicon.ico")
    public ApiResult<String> returnNoFavicon() {
        return ApiResult.ok();
    }
}
