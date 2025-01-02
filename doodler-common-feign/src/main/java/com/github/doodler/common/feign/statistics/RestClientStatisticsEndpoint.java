package com.github.doodler.common.feign.statistics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.timeseries.Sampler;

/**
 * @Description: RestClientStatisticEndpoint
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "restClientStatistics")
public class RestClientStatisticsEndpoint {

    @Autowired
    private RestClientStatisticsService statisticsService;

    @Autowired
    private LatestRequestHistoryCollector latestRequestHistoryCollector;

    @GetMapping("/sampler")
    public ApiResult<HttpSample> sampler(@RequestParam("catalog") String catalog,
            @RequestParam("dimension") String dimension) {
        Sampler<HttpSample> sampler =
                statisticsService.sampler(catalog, dimension, System.currentTimeMillis());
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/sequence")
    public ApiResult<Map<String, Object>> sequence(@RequestParam("catalog") String catalog,
            @RequestParam("dimension") String dimension) {
        Map<String, Object> data = statisticsService.sequence(catalog, dimension, null);
        return ApiResult.ok(data);
    }

    @GetMapping("/summarize")
    public ApiResult<HttpSample> summarize(@RequestParam("catalog") String catalog,
            @RequestParam("dimension") String dimension) {
        Sampler<HttpSample> sampler = statisticsService.summarize(catalog, dimension);
        return ApiResult.ok(sampler.getSample());
    }

    @GetMapping("/summarizeAll")
    public ApiResult<Map<String, HttpSample>> summarizeAll(@RequestParam("name") String name) {
        Map<String, Sampler<HttpSample>> samplers = statisticsService.summarize(name);
        return ApiResult.ok(samplers.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue().getSample()), LinkedHashMap::putAll));
    }

    @GetMapping("/showHistory")
    public ApiResult<List<RequestHistory>> showHistory() {
        return ApiResult.ok(latestRequestHistoryCollector.showHistory());
    }

    @GetMapping("/showErrorHistory")
    public ApiResult<List<RequestHistory>> showErrorHistory() {
        return ApiResult.ok(latestRequestHistoryCollector.showErrorHistory());
    }
}
