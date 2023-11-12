package io.doodler.common.mybatis.statistics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.doodler.common.ApiResult;
import io.doodler.common.utils.statistics.Sampler;

/**
 * @Description: MyBatisStatisticsEndpoint
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "myBatisStatistics")
public class MyBatisStatisticsEndpoint {

    @Autowired
    private MyBatisStatisticsService statisticsService;

    @GetMapping("/sampler")
    public ApiResult<SqlSample> sampler(@RequestParam("identifier") String identifier) {
        Sampler<SqlSample> sampler = statisticsService.sampler("sql_command", identifier, System.currentTimeMillis());
        return ApiResult.<SqlSample>ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, SqlSample>> sequence(@RequestParam("identifier") String identifier) {
        Map<String, Sampler<SqlSample>> samplers = statisticsService.sequence("sql_command", identifier);
        return ApiResult.ok(samplers.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue().getSample()), LinkedHashMap::putAll));
    }
    
    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, SqlSample>> summarizeAll() {
        Map<String, Sampler<SqlSample>> samplers = statisticsService.summarize("sql_command");
        return ApiResult.ok(samplers.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue().getSample()), LinkedHashMap::putAll));
    }
    
    @GetMapping("/summarize")
    public ApiResult<SqlSample> summarize(@RequestParam("identifier") String identifier) {
    	Sampler<SqlSample> sampler = statisticsService.summarize("sql_command", identifier);
    	return ApiResult.<SqlSample>ok(sampler.getSample());
    }
}