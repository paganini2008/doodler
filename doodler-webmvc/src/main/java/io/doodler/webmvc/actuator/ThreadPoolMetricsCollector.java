package io.doodler.webmvc.actuator;

import java.util.concurrent.BlockingQueue;
import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import cn.hutool.core.net.NetUtil;
import io.doodler.common.context.MetricsCollector;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: ThreadPoolMetricsCollector
 * @Author: Fred Feng
 * @Date: 14/02/2023
 * @Version 1.0.0
 */
public class ThreadPoolMetricsCollector implements InitializingBean, MetricsCollector {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final ThreadPoolTaskScheduler taskScheduler;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshMetrics();
    }

    @Override
    public void refreshMetrics() throws Exception {
        // Collect data from Task Executor
        createGauge(taskExecutor, "task_executor_pool_size", "Current Pool Size", taskExecutor -> (double) taskExecutor.getPoolSize());
        createGauge(taskExecutor, "task_executor_active_count", "Current Active Size",
                taskExecutor -> (double) taskExecutor.getActiveCount());
        createGauge(taskExecutor, "task_executor_completed_task_count", "Completed Task Count",
                taskExecutor -> (double) taskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        createGauge(taskExecutor, "task_executor_task_count", "Task Count",
                taskExecutor -> (double) taskExecutor.getThreadPoolExecutor().getTaskCount());
        BlockingQueue<Runnable> queue = taskExecutor.getThreadPoolExecutor().getQueue();
        createGauge(taskExecutor, "task_executor_remaining_capacity", "Remaining Capacity",
                taskExecutor -> (double) queue.remainingCapacity());
        createGauge(taskExecutor, "task_executor_queue_size", "Remaining Capacity", taskExecutor -> (double) queue.size());

        // Collect data from Task Scheduler
        createGauge(taskScheduler, "task_scheduler_pool_size", "Current Pool Size", taskScheduler -> (double) taskScheduler.getPoolSize());
        createGauge(taskScheduler, "task_scheduler_active_count", "Current Active Size",
                taskScheduler -> (double) taskScheduler.getActiveCount());
        createGauge(taskScheduler, "task_scheduler_completed_task_count", "Completed Task Count",
                taskScheduler -> (double) taskScheduler.getScheduledThreadPoolExecutor().getCompletedTaskCount());
        createGauge(taskScheduler, "task_scheduler_task_count", "Task Count",
                taskScheduler -> (double) taskScheduler.getScheduledThreadPoolExecutor().getTaskCount());
        BlockingQueue<Runnable> queue2 = taskScheduler.getScheduledThreadPoolExecutor().getQueue();
        createGauge(taskScheduler, "task_scheduler_remaining_capacity", "Remaining Capacity",
                taskScheduler -> (double) queue2.remainingCapacity());
        createGauge(taskScheduler, "task_scheduler_queue_size", "Remaining Capacity", taskScheduler -> (double) queue2.size());
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