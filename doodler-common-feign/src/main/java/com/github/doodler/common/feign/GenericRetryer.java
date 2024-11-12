package com.github.doodler.common.feign;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Observable;

import feign.RetryableException;
import feign.Retryer;

/**
 * @Description: GenericRetryer
 * @Author: Fred Feng
 * @Date: 27/09/2023
 * @Version 1.0.0
 */
public class GenericRetryer implements Retryer {

    private final int maxAttempts;
    private final long period;
    private final long maxPeriod;
    int attempt;
    long sleptForMillis;

    public GenericRetryer() {
        this(100, SECONDS.toMillis(1), 5);
    }

    public GenericRetryer(long period, long maxPeriod, int maxAttempts) {
        this.period = period;
        this.maxPeriod = maxPeriod;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }
    
    private DefaultObservable obs = new DefaultObservable();
    
    void setObservable(DefaultObservable obs) {
		this.obs = obs;
	}

	private static class DefaultObservable extends Observable {

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }
    }

    public void addRetryFailureHandler(RetryFailureHandler handler) {
    	if(handler!=null) {
        	obs.addObserver((ob,arg)->{
        		handler.onRetryFailed((RetryableException)arg);
        	});
    	}
    }
    
    // visible for testing;
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
        	obs.notifyObservers(e);
            throw e;
        }

        long interval;
        if (e.retryAfter() != null) {
            interval = e.retryAfter().getTime() - currentTimeMillis();
            if (interval > maxPeriod) {
                interval = maxPeriod;
            }
            if (interval < 0) {
                return;
            }
        } else {
            interval = nextMaxInterval();
        }
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }
        sleptForMillis += interval;
    }

    /**
     * Calculates the time interval to a retry attempt. <br>
     * The interval increases exponentially with each attempt, at a rate of nextInterval *= 1.5
     * (where 1.5 is the backoff factor), to the maximum interval.
     *
     * @return time in milliseconds from now until the next attempt.
     */
    long nextMaxInterval() {
        long interval = (long) (period * Math.pow(1.5, attempt - 1));
        return interval > maxPeriod ? maxPeriod : interval;
    }

    @Override
    public Retryer clone() {
    	GenericRetryer retryer = new GenericRetryer(period, maxPeriod, maxAttempts);
    	retryer.setObservable(obs);
    	return retryer;
    }
}