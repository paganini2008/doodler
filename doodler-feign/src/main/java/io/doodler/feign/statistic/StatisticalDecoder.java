package io.doodler.feign.statistic;

import static io.doodler.feign.RestClientConstants.REQUEST_HEADER_TIMESTAMP;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.HttpStatus;

import feign.Request;
import feign.Response;
import feign.codec.Decoder;
import io.doodler.feign.HttpUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: StatisticalDecoder
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class StatisticalDecoder implements Decoder {

    private final Decoder delegate;
    private final RestClientStatistics restClientStatistics;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        try {
            return delegate.decode(response, type);
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