package com.github.doodler.common.amqp.eventbus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.google.common.eventbus.Subscribe;

/**
 * @Description: EventBusCrossCutting
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Aspect
public class EventBusCrossCutting {

    @Pointcut("execution(public * *(..))")
    public void signature() {
    }

    @Around("signature() && @annotation(subscribe)")
    public Object arround(ProceedingJoinPoint pjp, Subscribe subscribe) throws Throwable {
        EventContext context = EventContext.getContext();
        try {
            Object result = pjp.proceed();
            context.commit();
            return result;
        } catch (Throwable e) {
            context.reject();
            throw e;
        } finally {
            EventContext.reset();
        }
    }
}