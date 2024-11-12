package com.github.doodler.common.feign.statistics;

import static com.github.doodler.common.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;
import java.net.URI;
import com.github.doodler.common.enums.AppName;
import com.github.doodler.common.feign.MappedRequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

/**
 * @Description: PerRequestStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class PerRequestStatisticsCollector implements MappedRequestInterceptor {
	
	private final RestClientStatisticsService statisticsService;

    @Override
    public void apply(RequestTemplate template) {
    	long startTime = System.currentTimeMillis();
    	if (!template.headers().containsKey(REQUEST_HEADER_TIMESTAMP)) {
    		template.header(REQUEST_HEADER_TIMESTAMP, String.valueOf(startTime));
    	}
    	
    	String url = template.url();
        statisticsService.prepare("action", url, startTime, sampler-> {
        	sampler.getSample().concurrents.increment();
        });
        
    	String hostName = URI.create(template.feignTarget().url()).getHost();
    	AppName appName = AppName.get(hostName);
        statisticsService.prepare("all", appName.getContextPath() + "/**", startTime, sampler-> {
        	sampler.getSample().concurrents.increment();
        });
    }
}