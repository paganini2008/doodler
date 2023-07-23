package io.doodler.feign.statistic;

import feign.FeignException;
import feign.RequestLine;
import io.doodler.common.BizException;
import io.doodler.feign.HttpUtils;
import io.doodler.feign.RestClientInvokerAspect;

import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * @Description: ConciseRestClientStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class ConciseRestClientStatisticsCollector extends AbstractRestClientStatisticsCollector implements
        RestClientInvokerAspect {

    @Override
    public void beforeInvoke(Method method, Object[] args, Map<String, Object> attributes) {
        long startTime = System.currentTimeMillis();
        attributes.put("startTime", startTime);
        RequestLine requestLine = method.getAnnotation(RequestLine.class);
        getRestClientStatistics().beginStatistic(requestLine.value());
    }

    @Override
    public void afterInvoke(Method method, Object[] args, Map<String, Object> attributes, Throwable e) {
        long startTime = (Long) attributes.get("startTime");
        long elapsedTime = System.currentTimeMillis() - startTime;
        RequestLine requestLine = method.getAnnotation(RequestLine.class);
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
        getRestClientStatistics().endStatistic(requestLine.value(), httpStatus, elapsedTime);
    }
}