package io.doodler.webmvc;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.doodler.common.context.AsyncCallableErrorHandler;
import io.doodler.webmvc.actuator.ThreadPoolMetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: ThreadPoolConfig
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
public class ThreadPoolConfig {

    @Bean
    public TaskExecutorCustomizer taskExecutorCustomizer(ThreadPoolProperties config, ErrorHandler errorHandler) {
        return executor -> {
            final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat(config.getExecutor().getThreadNameFormat())
                    .setDaemon(false)
                    .setPriority(Thread.NORM_PRIORITY)
                    .setUncaughtExceptionHandler((t, e) -> {
                        errorHandler.handleError(e);
                    }).build();
            executor.setCorePoolSize(config.getExecutor().getPoolSize());
            executor.setMaxPoolSize(config.getExecutor().getMaxPoolSize());
            executor.setQueueCapacity(config.getExecutor().getQueueCapacity());
            executor.setThreadFactory(threadFactory);
            executor.setThreadGroupName(config.getExecutor().getThreadGroupName());
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setWaitForTasksToCompleteOnShutdown(true);
            executor.setAwaitTerminationSeconds(60);
        };
    }

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer(ThreadPoolProperties config, ErrorHandler errorHandler) {
        return scheduler -> {
            final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat(config.getScheduler().getThreadNameFormat())
                    .setDaemon(false)
                    .setPriority(Thread.NORM_PRIORITY)
                    .setUncaughtExceptionHandler((t, e) -> {
                        errorHandler.handleError(e);
                    }).build();
            scheduler.setPoolSize(config.getScheduler().getPoolSize());
            scheduler.setThreadFactory(threadFactory);
            scheduler.setThreadGroupName(config.getScheduler().getThreadGroupName());
            scheduler.setAwaitTerminationSeconds(60);
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
            scheduler.setErrorHandler(errorHandler);
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorHandler defaultErrorHandler() {
        return new AsyncCallableErrorHandler();
    }

    @Bean
    public ThreadPoolMetricsCollector threadPoolMetricsCollector(ThreadPoolTaskExecutor taskExecutor,
                                                                 ThreadPoolTaskScheduler taskScheduler,
                                                                 MeterRegistry registry) {
        return new ThreadPoolMetricsCollector(taskExecutor, taskScheduler, registry);
    }
}