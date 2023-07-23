package io.doodler.feign.statistic;

import org.springframework.http.HttpStatus;

/**
 * @Description: RestClientStatistics
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public interface RestClientStatistics {
    
    int DEFAULT_LATEST_REQUEST_COLLECTING_SIZE = 10;

    void beginStatistic(String requestLine);

    void endStatistic(String requestLine, HttpStatus httpStatus, long elapsedTime);
}