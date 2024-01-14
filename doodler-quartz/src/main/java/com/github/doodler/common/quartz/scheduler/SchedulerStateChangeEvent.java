package com.github.doodler.common.quartz.scheduler;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * @Description: SchedulerStateChangeEvent
 * @Author: Fred Feng
 * @Date: 28/09/2023
 * @Version 1.0.0
 */
@Getter
public class SchedulerStateChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4572853039782420144L;

    private final SchedulerStateEventType schedulerStateEventType;
    private final Object parameter;

    public SchedulerStateChangeEvent(Object source, Object parameter, SchedulerStateEventType schedulerStateEventType) {
        super(source);
        this.parameter = parameter;
        this.schedulerStateEventType = schedulerStateEventType;
        
    }
}