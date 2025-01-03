package com.github.doodler.common.zk.scheduling;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.github.doodler.common.zk.election.LeaderElectionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: CrosscuttingSchedulingTasks
 * @Author: Fred Feng
 * @Date: 14/08/2024
 * @Version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CrosscuttingSchedulingTasks {

    private final LeaderElectionContext leaderElectionContext;

    @Pointcut("execution(public * *(..))")
    public void signature() {}

    @Around("signature() && @annotation(scheduled)")
    public Object arround(ProceedingJoinPoint pjp, Scheduled scheduled) throws Throwable {
        try {
            if (leaderElectionContext.isLeader()) {
                if (log.isTraceEnabled()) {
                    log.trace("Run @Scheduled execution {}", pjp.getSignature().toString());
                }
                return pjp.proceed();
            }
            if (log.isTraceEnabled()) {
                log.trace("Waiting for running @Scheduled execution {}",
                        pjp.getSignature().toString());
            }
            return null;
        } catch (Throwable e) {
            throw e;
        } finally {

        }
    }

}
