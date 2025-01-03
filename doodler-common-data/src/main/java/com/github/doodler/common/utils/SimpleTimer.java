package com.github.doodler.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;
import com.github.doodler.common.context.ManagedBeanLifeCycle;

/**
 * @Description: SimpleTimer
 * @Author: Fred Feng
 * @Date: 14/02/2023
 * @Version 1.0.0
 */
public abstract class SimpleTimer
        implements Runnable, ManagedBeanLifeCycle, ApplicationListener<ApplicationReadyEvent> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;
    private final AtomicBoolean running;
    private final AtomicBoolean quickStart;

    public SimpleTimer(long period, TimeUnit timeUnit) {
        this(period, period, timeUnit);
    }

    public SimpleTimer(long initialDelay, long period, TimeUnit timeUnit) {
        this(initialDelay, period, timeUnit, true);
    }

    protected SimpleTimer(long initialDelay, long period, TimeUnit timeUnit, boolean quickStart) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
        this.quickStart = new AtomicBoolean(quickStart);
        this.running = new AtomicBoolean(false);
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
            throw new IllegalStateException("SimpleTimer is running, please stop it first.");
        }
        if (future == null) {
            running.set(true);
            if (executor == null || ExecutorUtils.isShutdown(executor)) {
                this.executor = Executors.newSingleThreadScheduledExecutor();
                this.autoClose = true;
            }
            this.future = executor.scheduleWithFixedDelay(this, initialDelay, period, timeUnit);
            if (log.isInfoEnabled()) {
                log.info(
                        "{} will be running periodically with initialDelay: {}, period: {}, timeUnit: {}",
                        getClass().getName(), initialDelay, period, timeUnit);
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
            throw new IllegalStateException("SimpleTimer has been stoped.");
        }
        if (future != null) {
            future.cancel(true);
            future = null;

            running.set(false);
        }
        if (autoClose && executor != null) {
            ExecutorUtils.gracefulShutdown(executor, 60000L);
        }
        quickStart.set(false);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (quickStart.get()) {
            start();
        } else {
            log.info("{} will start working after application being ready", getClass().getName());
        }
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

    protected void handleCancellation(@Nullable Exception reason) {}

    public abstract boolean change() throws Exception;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!quickStart.get()) {
            start();
        }
    }

}
