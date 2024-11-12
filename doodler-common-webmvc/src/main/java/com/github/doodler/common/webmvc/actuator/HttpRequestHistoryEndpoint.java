package com.github.doodler.common.webmvc.actuator;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.HttpRequestInfo;

/**
 * @Description: HttpRequestHistoryEndpoint
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
@Component
@RestControllerEndpoint(id = "httpRequestHistory")
public class HttpRequestHistoryEndpoint {

    @Autowired
    private HttpRequestHistoryCollector latestRequestHistoryCollector;

    @GetMapping("/showHistory")
    public ApiResult<List<HttpRequestInfo>> showHistory() {
        return ApiResult.ok(latestRequestHistoryCollector.showHistory());
    }

    @GetMapping("/showErrorHistory")
    public ApiResult<List<HttpRequestInfo>> showErrorHistory() {
        return ApiResult.ok(latestRequestHistoryCollector.showErrorHistory());
    }
}