package io.doodler.feign.statistic;

import static io.doodler.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.doodler.feign.HttpUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: ExactRestClientStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ExactRestClientStatisticsCollector extends AbstractRestClientStatisticsCollector implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
    	if (!template.headers().containsKey(REQUEST_HEADER_TIMESTAMP)) {
    		template.header(REQUEST_HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    	}
        String requestLine = HttpUtils.getRequestLine(template.request());
        getRestClientStatistics().beginStatistic(requestLine);
    }
}