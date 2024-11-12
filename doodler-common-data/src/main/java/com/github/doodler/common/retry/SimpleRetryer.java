package com.github.doodler.common.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.utils.SimpleTimer;

/**
 * @Description: SimpleRetryer
 * @Author: Fred Feng
 * @Date: 12/10/2023
 * @Version 1.0.0
 */
public class SimpleRetryer extends SimpleTimer {

	public SimpleRetryer(long period, TimeUnit timeUnit, RetryQueue retryQueue) {
		this(period, period, timeUnit, retryQueue);
	}

	public SimpleRetryer(long initialDelay, long period, TimeUnit timeUnit, RetryQueue retryQueue) {
		super(initialDelay, period, timeUnit);
		this.retryQueue = retryQueue;
	}

	private final RetryQueue retryQueue;
	
	public void retry(Retryable retryable) {
		retryQueue.putObject(retryable);
	}
	
	public int pendingRetryCount() {
		return retryQueue.size();
	}

	@Override
	public boolean change() throws Exception {
		if(pendingRetryCount() == 0) {
			return true;
		}
		List<Object> list = new ArrayList<>();
		retryQueue.drainTo(list);
		for (Object obj : list) {
			if (obj instanceof Retryable) {
				Retryable retryable = (Retryable) obj;
				try {
					retryable.retry();
				} catch (Exception e) {
					if (!retryable.discontinue(e)) {
						retryQueue.putObject(obj);
					}
				}
			}
		}
		return true;
	}
}