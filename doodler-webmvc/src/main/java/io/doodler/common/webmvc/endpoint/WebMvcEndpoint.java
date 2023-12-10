package io.doodler.common.webmvc.endpoint;

import io.doodler.common.ApiResult;
import io.doodler.common.Constants;
import io.doodler.common.context.InstanceId;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: WebMvcEndpoint
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@RestController
public class WebMvcEndpoint {

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
    public ApiResult<String> ping() {
        return ApiResult.ok(instanceId.isStandby() ? "UP" : "DOWN");
    }

    @GetMapping("/favicon.ico")
    public ApiResult<String> returnNoFavicon() {
        return ApiResult.ok();
    }
}