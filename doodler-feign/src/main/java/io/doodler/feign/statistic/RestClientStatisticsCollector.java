package io.doodler.feign.statistic;

import java.util.Map;

/**
 * @Description: RestClientStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public interface RestClientStatisticsCollector {

    void incrementTotalExecution(String requestLine);

    void incrementSuccessExecution(String requestLine);
    
    void incrementConcurrents(String requestLine);

    void decrementConcurrents(String requestLine);

    void accumulateExecutionTime(String requestLine, long responseTime);
    
    RequestStatistics statistics(String requestLine);

    Map<String, RequestStatistics> statistics();
}