package com.github.doodler.common.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import com.github.doodler.common.cloud.PrimaryApplicationInfoReadyEvent;
import com.github.doodler.common.cloud.SecondaryApplicationInfoRefreshEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: CrosscuttingScheduling
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
@Slf4j
@Aspect
public class CrosscuttingScheduling {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Pointcut("execution(public * *(..))")
    public void signature() {}

    @Around("signature() && @annotation(scheduled)")
    public Object arround(ProceedingJoinPoint pjp, Scheduled scheduled) throws Throwable {
        if (!isStarted()) {
            log.trace("ScheduledTasks are idle for running");
            return null;
        }
        log.trace("ScheduledTasks are running on target: {}", pjp.getSignature().toString());
        return pjp.proceed();
    }

    public boolean isStarted() {
        return started.get();
    }

    @EventListener({PrimaryApplicationInfoReadyEvent.class})
    public void onPrimaryApplicationInfoReadyEvent() {
        started.set(true);
    }

    @EventListener({SecondaryApplicationInfoRefreshEvent.class})
    public void onSecondaryApplicationInfoRefreshEvent() {
        started.set(false);
    }
}
