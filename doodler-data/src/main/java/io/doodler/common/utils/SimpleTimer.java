package io.doodler.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import io.doodler.common.context.ManagedBeanLifeCycle;

/**
 * @Description: SimpleTimer
 * @Author: Fred Feng
 * @Date: 14/02/2023
 * @Version 1.0.0
 */
public abstract class SimpleTimer implements Runnable, ManagedBeanLifeCycle {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;
    private final AtomicBoolean running = new AtomicBoolean();

    public SimpleTimer(long period, TimeUnit timeUnit) {
        this(period, period, timeUnit);
    }

    public SimpleTimer(long initialDelay, long period, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> future;
    private ErrorHandler errorHandler;
    private boolean runNow;

    public void setRunNow(boolean runNow) {
		this.runNow = runNow;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void start() {
        if (isRunning()) {
            throw new IllegalStateException("Timer is running, please stop it first.");
        }
        if (future == null) {
            if (executor == null || ExecutorUtils.isShutdown(executor)) {
                this.executor = Executors.newSingleThreadScheduledExecutor();
            }
            this.future = executor.scheduleWithFixedDelay(this, initialDelay, period, timeUnit);
            this.running.set(true);
        }
        if(runNow) {
        	run();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;

            running.set(false);
        }
        ExecutorUtils.gracefulShutdown(executor, 60000L);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
        log.info("Start timer for '{}'", getClass());
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void run() {
        boolean result = false;
        try {
            result = change();
        } catch (Exception e) {
        	result = handleError(e);
        } finally {
            if (!result) {
                stop();
                handleCancellation();
            }
        }
    }

    protected boolean handleError(Exception e) {
        if (errorHandler != null) {
            errorHandler.handleError(e);
        } else {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return true;
    }

    protected void handleCancellation() {
    }

    public abstract boolean change() throws Exception;
}