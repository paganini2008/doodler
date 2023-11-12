package io.doodler.common.feign.statistics;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Description: SimpleTpsUpdater
 * @Author: Fred Feng
 * @Date: 22/09/2023
 * @Version 1.0.0
 */
public class SimpleTpsUpdater implements TpsUpdater {

	private final LongAdder counter;

	public SimpleTpsUpdater() {
		this.counter = new LongAdder();
	}

	private volatile long latestCount;
	private volatile int tps;

	@Override
	public void incr() {
		counter.increment();
	}

	@Override
	public int get() {
		return tps;
	}

	@Override
	public void set() {
		long currentCount = counter.longValue();
		if (currentCount > 0) {
			tps = (int) (currentCount - latestCount);
			latestCount = currentCount;
		}
	}
}