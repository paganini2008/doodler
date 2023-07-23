package io.doodler.feign.statistic;

import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.doodler.feign.HttpUtils;
import lombok.RequiredArgsConstructor;

import static io.doodler.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;

import org.springframework.http.HttpStatus;

/**
 * @Description: StatisticalErrorDecoder
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class StatisticalErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder delegate;
    private final RestClientStatistics restClientStatistics;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            return delegate.decode(methodKey, response);
        } finally {
            doStatistics(response);
        }
    }

    private void doStatistics(Response response) {
        Request request = response.request();
        String requestLine = HttpUtils.getRequestLine(request);
        HttpStatus httpStatus = HttpUtils.getHttpStatus(response.status());
        long elapsedTime;
        try {
            long startTime = Long.parseLong(
                    HttpUtils.getFirstHeader(request, REQUEST_HEADER_TIMESTAMP));
            elapsedTime = System.currentTimeMillis() - startTime;
        } catch (RuntimeException e) {
            elapsedTime = 0;
        }
        restClientStatistics.endStatistic(requestLine, httpStatus, elapsedTime);
    }
}