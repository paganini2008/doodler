package com.github.doodler.common.webmvc.actuator;

import java.util.concurrent.BlockingQueue;
import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import com.github.doodler.common.context.MetricsCollector;
import cn.hutool.core.net.NetUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ThreadPoolMetricsCollector
 * @Author: Fred Feng
 * @Date: 14/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class ThreadPoolMetricsCollector implements MetricsCollector {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final @Nullable ThreadPoolTaskScheduler taskScheduler;
    private final MeterRegistry registry;

    public ThreadPoolMetricsCollector(ThreadPoolTaskExecutor taskExecutor,
                                      ThreadPoolTaskScheduler taskScheduler,
                                      MeterRegistry registry) {
        this.taskExecutor = taskExecutor;
        this.taskScheduler = taskScheduler;
        this.registry = registry;
    }

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${server.port}")
    private int port;

    private final String localHost = NetUtil.getLocalhostStr();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws Exception {
        refreshMetrics();
        if (log.isTraceEnabled()) {
            log.trace("Start to collect metrics and push to MeterRegistry ...");
        }
    }

    @Override
    public void refreshMetrics() throws Exception {
        // Collect data from Task Executor
        if (taskExecutor != null) {
            createGauge(taskExecutor, "threads.executor.pool.size", "Current Pool Size",
                    taskExecutor -> (double) taskExecutor.getPoolSize());
            createGauge(taskExecutor, "threads.executor.active.count", "Current Active Size",
                    taskExecutor -> (double) taskExecutor.getActiveCount());
            createGauge(taskExecutor, "threads.executor.completed.task.count", "Completed Task Count",
                    taskExecutor -> (double) taskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
            createGauge(taskExecutor, "threads.executor.task.count", "Task Count",
                    taskExecutor -> (double) taskExecutor.getThreadPoolExecutor().getTaskCount());
            
            BlockingQueue<Runnable> queue = taskExecutor.getThreadPoolExecutor().getQueue();
            createGauge(taskExecutor, "threads.executor.queue.remaining.capacity", "Queue Remaining Capacity",
                    taskExecutor -> (double) queue.remainingCapacity());
            createGauge(taskExecutor, "threads.executor.queue.size", "Queue Size",
                    taskExecutor -> (double) queue.size());
        }

        // Collect data from Task Scheduler
        if (taskScheduler != null) {
            createGauge(taskScheduler, "threads.scheduler.pool.size", "Current Pool Size",
                    taskScheduler -> (double) taskScheduler.getPoolSize());
            createGauge(taskScheduler, "threads.scheduler.active.count", "Current Active Size",
                    taskScheduler -> (double) taskScheduler.getActiveCount());
            createGauge(taskScheduler, "threads.scheduler.completed.task.count", "Completed Task Count",
                    taskScheduler -> (double) taskScheduler.getScheduledThreadPoolExecutor().getCompletedTaskCount());
            createGauge(taskScheduler, "threads.scheduler.task.count", "Task Count",
                    taskScheduler -> (double) taskScheduler.getScheduledThreadPoolExecutor().getTaskCount());
            
            BlockingQueue<Runnable> queue2 = taskScheduler.getScheduledThreadPoolExecutor().getQueue();
            createGauge(taskScheduler, "threads.scheduler.queue.remaining.capacity", "Queue Remaining Capacity",
                    taskScheduler -> (double) queue2.remainingCapacity());
            createGauge(taskScheduler, "threads.scheduler.queue.size", "Queue Size",
                    taskScheduler -> (double) queue2.size());
        }
    }

    private void createGauge(ThreadPoolTaskExecutor ref, String metric, String help,
                             ToDoubleFunction<ThreadPoolTaskExecutor> measure) {
        Gauge.builder(metric, ref, measure)
                .description(help)
                .tag("pool", "task-executor")
                .tag("env", env)
                .tag("instance", localHost + ":" + port)
                .register(this.registry);
    }

    private void createGauge(ThreadPoolTaskScheduler ref, String metric, String help,
                             ToDoubleFunction<ThreadPoolTaskScheduler> measure) {
        Gauge.builder(metric, ref, measure)
                .description(help)
                .tag("pool", "task-scheduler")
                .tag("env", env)
                .tag("instance", localHost + ":" + port)
                .register(this.registry);
    }
}