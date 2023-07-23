package io.doodler.feign.statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: AbstractRestClientStatisticsCollector
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public abstract class AbstractRestClientStatisticsCollector implements RestClientStatisticsCollector {

	private final RestClientStatistics restClientStatistics = new DefaultRestClientStatistics(this);
    private final ConcurrentMap<String, RequestStatistics> statistics = new ConcurrentHashMap<>();

    @Override
    public void incrementTotalExecution(String requestLine) {
        statistics(requestLine).totalExecutions.increment();
    }

    @Override
    public void incrementSuccessExecution(String requestLine) {
        statistics(requestLine).successExecutions.increment();
    }
    
    @Override
    public void incrementConcurrents(String requestLine) {
        statistics(requestLine).concurrents.increment();
    }

    @Override
    public void decrementConcurrents(String requestLine) {
        statistics(requestLine).concurrents.decrement();
    }

    @Override
    public void accumulateExecutionTime(String requestLine, long responseTime) {
        statistics(requestLine).accumulatedExecutionTime.add(responseTime);
    }

    @Override
    public Map<String, RequestStatistics> statistics() {
        return new HashMap<>(statistics);
    }

    @Override
    public RequestStatistics statistics(String requestLine) {
        RequestStatistics requestStatistics = statistics.get(requestLine);
        if (requestStatistics == null) {
            statistics.putIfAbsent(requestLine, new RequestStatistics());
            requestStatistics = statistics.get(requestLine);
        }
        return requestStatistics;
    }
    
    public RestClientStatistics getRestClientStatistics() {
		return restClientStatistics;
	}

}