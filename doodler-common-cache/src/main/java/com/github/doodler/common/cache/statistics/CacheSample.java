package com.github.doodler.common.cache.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.LongAdder;
import com.github.doodler.common.timeseries.Metric;

/**
 * @Description: CacheSample
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class CacheSample implements Metric {

    final LongAdder gets = new LongAdder();
    final LongAdder hits = new LongAdder();
    final LongAdder puts = new LongAdder();
    final LongAdder evicts = new LongAdder();

    public long getGetCount() {
        return gets.longValue();
    }

    public long getHitCount() {
        return hits.longValue();
    }

    public long getPutCount() {
        return puts.longValue();
    }

    public long getEvictCount() {
        return evicts.longValue();
    }

    public double getHitPercent() {
        long total = getGetCount();
        if (total == 0L) {
            return 0;
        }
        BigDecimal value = BigDecimal.valueOf((double) getHitCount() / total).setScale(4, RoundingMode.HALF_UP);
        value = value.multiply(BigDecimal.valueOf(100));
        return value.doubleValue();
    }

    public double getPutPercent() {
        long total = getGetCount();
        if (total == 0L) {
            return 0;
        }
        BigDecimal value = BigDecimal.valueOf((double) getPutCount() / total).setScale(4, RoundingMode.HALF_UP);
        value = value.multiply(BigDecimal.valueOf(100));
        return value.doubleValue();
    }
}