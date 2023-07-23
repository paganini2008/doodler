package io.doodler.feign.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @Description: DefaultRestClientStatistics
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultRestClientStatistics implements RestClientStatistics {

	private final RestClientStatisticsCollector restClientStatisticsCollector;

	@Override
	public void beginStatistic(String requestLine) {
		restClientStatisticsCollector.incrementConcurrents(requestLine);
	}

	@Override
	public void endStatistic(String requestLine, HttpStatus httpStatus, long elapsedTime) {
		restClientStatisticsCollector.incrementTotalExecution(requestLine);
		if (httpStatus.is2xxSuccessful()) {
			restClientStatisticsCollector.incrementSuccessExecution(requestLine);
		}
		restClientStatisticsCollector.accumulateExecutionTime(requestLine, elapsedTime);
		restClientStatisticsCollector.decrementConcurrents(requestLine);
	}
}