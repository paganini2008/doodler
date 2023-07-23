package io.doodler.feign.actuator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;

import io.doodler.common.ApiResult;
import io.doodler.feign.statistic.LatestRequestHistoryCollector;
import io.doodler.feign.statistic.RequestHistory;
import io.doodler.feign.statistic.RequestStatistics;
import io.doodler.feign.statistic.RestClientStatisticsCollector;

/**
 * @Description: RestClientStatisticEndpoint
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "restClientStatistics")
public class RestClientStatisticsEndpoint {

    @Autowired
    private RestClientStatisticsCollector restClientStatisticsCollector;

    @Autowired
    private LatestRequestHistoryCollector latestRequestHistoryCollector;

    @GetMapping("/getStatisticsInfo")
    public ApiResult<Map<String, RequestStatistics>> getStatisticsInfo() {
        return ApiResult.ok(restClientStatisticsCollector.statistics());
    }
    
    @GetMapping("/showHistory")
    public ApiResult<List<RequestHistory>> showHistory(){
    	return ApiResult.ok(latestRequestHistoryCollector.showHistory());
    }
    
    @GetMapping("/showErrorHistory")
    public ApiResult<List<RequestHistory>> showErrorHistory(){
    	return ApiResult.ok(latestRequestHistoryCollector.showErrorHistory());
    }
}