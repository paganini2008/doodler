package io.doodler.common.quartz.scheduler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: Counter
 * @Author: Fred Feng
 * @Date: 11/11/2023
 * @Version 1.0.0
 */
public final class Counter {

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
        return runningCounter.decrementAndGet();
    }

    public long getRunningCount() {
        return runningCounter.get();
    }
}