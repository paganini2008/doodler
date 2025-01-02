package com.github.doodler.common.mybatis.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.LongAdder;
import com.github.doodler.common.timeseries.Metric;

/**
 * @Description: SqlSample
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class SqlSample implements Metric {

    final LongAdder totalExecutions = new LongAdder();
    final LongAdder successExecutions = new LongAdder();
    final LongAdder slowExecutions = new LongAdder();
    final LongAdder accumulatedExecutionTime = new LongAdder();

    public long getTotalExecutionCount() {
        return totalExecutions.longValue();
    }

    public long getSuccessExecutionCount() {
        return successExecutions.longValue();
    }

    public long getSlowExecutionCount() {
        return slowExecutions.longValue();
    }

    public double getSlowPercent() {
        long total = getTotalExecutionCount();
        if (total == 0) {
            return 0L;
        }
        BigDecimal value = BigDecimal.valueOf((double) getSlowExecutionCount() / total).setScale(4, RoundingMode.HALF_UP);
        value = value.multiply(BigDecimal.valueOf(100));
        return value.doubleValue();
    }

    public double getSuccessPercent() {
        long total = getTotalExecutionCount();
        if (total == 0) {
            return 0L;
        }
        BigDecimal value = BigDecimal.valueOf((double) getSuccessExecutionCount() / total).setScale(4,
                RoundingMode.HALF_UP);
        value = value.multiply(BigDecimal.valueOf(100));
        return value.doubleValue();
    }

    public long getAverageExecutionTime() {
        long total = getTotalExecutionCount();
        if (total == 0) {
            return 0L;
        }
        return accumulatedExecutionTime.longValue() / total;
    }
}