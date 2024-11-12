package com.github.doodler.common.quartz.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * @Description: JobSchedulerInitializer
 * @Author: Fred Feng
 * @Date: 16/06/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class JobSchedulerInitializer extends SchedulerListenerSupport implements InitializingBean {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired(required = false)
    private SchedulerCustomizer schedulerCustomizer;

    private final AtomicBoolean started = new AtomicBoolean();

    @Override
    public void afterPropertiesSet() throws Exception {
        schedulerFactoryBean.getScheduler().getListenerManager().addSchedulerListener(this);
        if (schedulerCustomizer != null) {
            schedulerCustomizer.customize(schedulerFactoryBean.getScheduler());
        }
    }

    @Override
    public void schedulerStarted() {
        super.schedulerStarted();
        started.set(true);
        log.info("DefaultQuartzScheduler is started now...");
    }

    @Override
    public void schedulerShuttingdown() {
        super.schedulerShuttingdown();
        started.set(false);
        log.info("DefaultQuartzScheduler is shutting down.");
    }

    public boolean isStarted() {
        return started.get();
    }
}