package io.doodler.feign.statistic;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.LongAdder;

/**
 * 
 * @Description: RequestStatistics
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class RequestStatistics {

	final LongAdder totalExecutions = new LongAdder();
    final LongAdder successExecutions = new LongAdder();
    final LongAdder concurrents = new LongAdder();
    final LongAdder accumulatedExecutionTime = new LongAdder();

    public long getTotalExecutions() {
        return totalExecutions.longValue();
    }

    public long getSuccessExecutions() {
        return successExecutions.longValue();
    }

    public long getConcurrents() {
        return concurrents.longValue();
    }

    public long getAverageExecutionTime() {
        long total = getTotalExecutions();
        if (total == 0) {
            return 0L;
        }
        return accumulatedExecutionTime.longValue() / total;
    }

    public String getFailureRatio() {
        long total = getTotalExecutions();
        long success = getSuccessExecutions();
        if (total == 0) {
            return "-";
        }
        return new DecimalFormat("0.#%").format((double) (total - success) / total);
    }
	
}
