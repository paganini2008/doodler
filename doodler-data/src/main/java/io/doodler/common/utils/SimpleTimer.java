package io.doodler.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
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
    private @Nullable ErrorHandler errorHandler;
    private boolean runImmediatedly;
    private boolean autoClose;

    public void setRunImmediatedly(boolean runImmediatedly) {
        this.runImmediatedly = runImmediatedly;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
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
                this.autoClose = true;
            }
            this.future = executor.scheduleWithFixedDelay(this, initialDelay, period, timeUnit);
            this.running.set(true);
            if (log.isInfoEnabled()) {
                log.info("Periodically run {} with parameters: {}, {}, {}", getClass().getSimpleName(), initialDelay,
                        period, timeUnit);
            }
        }
        if (runImmediatedly) {
            run();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("Timer has been stoped.");
        }
        if (future != null) {
            future.cancel(true);
            future = null;

            running.set(false);
        }
        if(autoClose) {
            ExecutorUtils.gracefulShutdown(executor, 60000L);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public final void run() {
        if (!isRunning()) {
            return;
        }
        boolean result = false;
        Exception reason = null;
        try {
            result = change();
        } catch (Exception e) {
            result = handleError(e);
            reason = e;
        } finally {
            if (!result) {
                stop();
                handleCancellation(reason);
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

    protected void handleCancellation(@Nullable Exception reason) {
    }

    public abstract boolean change() throws Exception;
}