package com.github.doodler.common.feign.statistics;

import static com.github.doodler.common.feign.RestClientConstants.DEFAULT_MAXIMUM_RESPONSE_TIME;
import static com.github.doodler.common.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;
import java.net.URI;

import org.springframework.http.HttpStatus;
import com.github.doodler.common.enums.AppName;
import com.github.doodler.common.feign.HttpUtils;
import com.github.doodler.common.feign.RetryFailureHandler;
import feign.Request;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;

/**
 * @Description: StatisticalRetryFailureHandler
 * @Author: Fred Feng
 * @Date: 27/09/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class StatisticalRetryFailureHandler implements RetryFailureHandler {

    private final RestClientStatisticsService statisticsService;

    @Override
    public void onRetryFailed(RetryableException reason) {
        Request request = reason.request();

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        long startTime;
        try {
            startTime = Long.parseLong(
                    HttpUtils.getFirstHeader(request, REQUEST_HEADER_TIMESTAMP));
        } catch (RuntimeException ignored) {
            startTime = System.currentTimeMillis();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean isOk = httpStatus.is2xxSuccessful();

        statisticsService.update("action", request.url(), startTime, sampler -> {
            HttpSample sample = sampler.getSample();
            sample.totalExecutions.increment();
            if (isOk) {
                sample.successExecutions.increment();
            }
            if (elapsedTime > DEFAULT_MAXIMUM_RESPONSE_TIME) {
                sample.slowExecutions.increment();
            }
            sample.accumulatedExecutionTime.add(elapsedTime);
            sample.concurrents.decrement();
        });

        String hostName = URI.create(request.requestTemplate().feignTarget().url()).getHost();
        AppName appName = AppName.get(hostName);
        statisticsService.update("all", appName.getContextPath() + "/**", startTime, sampler -> {
            HttpSample sample = sampler.getSample();
            sample.totalExecutions.increment();
            if (isOk) {
                sample.successExecutions.increment();
            }
            if (elapsedTime > DEFAULT_MAXIMUM_RESPONSE_TIME) {
                sample.slowExecutions.increment();
            }
            sample.accumulatedExecutionTime.add(elapsedTime);
            sample.concurrents.decrement();
        });
    }
}