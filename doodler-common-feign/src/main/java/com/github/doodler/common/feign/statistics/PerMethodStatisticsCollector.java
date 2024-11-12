package com.github.doodler.common.feign.statistics;

import com.github.doodler.common.BizException;
import com.github.doodler.common.feign.HttpUtils;
import com.github.doodler.common.feign.RestClientInvokerAspect;
import com.github.doodler.common.feign.RestClientMetadata;
import com.github.doodler.common.feign.RestClientMetadataCollector;
import feign.FeignException;
import static com.github.doodler.common.feign.RestClientConstants.DEFAULT_MAXIMUM_RESPONSE_TIME;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @Description: PerMethodStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class PerMethodStatisticsCollector implements RestClientInvokerAspect {

    private final RestClientMetadataCollector restClientMetadataCollector;
    private final RestClientStatisticsService statisticsService;

    @Override
    public void beforeInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes) {
        long startTime = System.currentTimeMillis();
        attributes.put("startTime", startTime);

        statisticsService.prepare("action", String.format("%s#%s(%d args)", apiInterfaceClass.getSimpleName(), method.getName(), method.getParameterCount()),
                startTime, sampler -> {
                    sampler.getSample().concurrents.increment();
                });

        RestClientMetadata metadata = restClientMetadataCollector.lookup(apiInterfaceClass);
        statisticsService.prepare("all", metadata.getServiceId(), startTime, sampler -> {
            sampler.getSample().concurrents.increment();
        });
    }

    @Override
    public void afterInvoke(Class<?> apiInterfaceClass, Method method, Object[] args, Map<String, Object> attributes,
                            Throwable e) {
        long startTime = (Long) attributes.get("startTime");
        long elapsedTime = System.currentTimeMillis() - startTime;
        HttpStatus httpStatus = HttpStatus.OK;
        if (e != null) {
            if (e instanceof BizException) {
                httpStatus = ((BizException) e).getHttpStatus();
            } else if (e instanceof FeignException) {
                httpStatus = HttpUtils.getHttpStatus((FeignException) e);
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        boolean isOk = httpStatus.is2xxSuccessful();
        statisticsService.prepare("action", String.format("%s#%s(%d args)", apiInterfaceClass.getSimpleName(), method.getName(), method.getParameterCount()),
                startTime, sampler -> {
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

        RestClientMetadata metadata = restClientMetadataCollector.lookup(apiInterfaceClass);
        if (metadata != null) {
            statisticsService.update("all", metadata.getServiceId(), startTime, sampler -> {
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
}