package com.github.doodler.common.quartz.statistics;

import java.util.concurrent.atomic.AtomicLong;
import com.github.doodler.common.timeseries.Metric;

/**
 * @Description: RuntimeCounter
 * @Author: Fred Feng
 * @Date: 11/11/2023
 * @Version 1.0.0
 */
public final class RuntimeCounter implements Metric {

    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong errorCounter = new AtomicLong();
    private final AtomicLong runningCounter = new AtomicLong();

    public long incrementCount() {
        return counter.incrementAndGet();
    }

    public long getCount() {
        return counter.get();
    }

    public long incrementErrorCount() {
        return errorCounter.incrementAndGet();
    }

    public long getErrorCount() {
        return errorCounter.get();
    }

    public long incrementRunningCount() {
        return runningCounter.incrementAndGet();
    }

    public long decrementRunningCount() {
        long l;
        if ((l = runningCounter.decrementAndGet()) < 0) {
            runningCounter.set(0);
            l = 0;
        }
        return l;
    }

    public long getRunningCount() {
        return runningCounter.get();
    }
}