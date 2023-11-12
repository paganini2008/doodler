package io.doodler.common.webmvc;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import io.doodler.common.context.AsyncErrorHandler;
import io.doodler.common.context.ConditionalOnNotApplication;
import io.doodler.common.webmvc.actuator.ThreadPoolMetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: ThreadPoolConfig
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ThreadPoolConfig {

    @Value("${spring.application.name}")
    private String applicatonName;

    @Bean
    public RequestContextTaskDecorator requestContextTaskDecorator() {
        return new RequestContextTaskDecorator();
    }

    @Bean
    public TaskExecutorCustomizer taskExecutorCustomizer(TaskExecutionProperties properties, ErrorHandler errorHandler) {
        return executor -> {
            TaskExecutionProperties.Pool pool = properties.getPool();
            executor.setCorePoolSize(Math.min(pool.getCoreSize(), 200));
            executor.setMaxPoolSize(Math.min(pool.getMaxSize(), 200));
            executor.setQueueCapacity(Math.min(pool.getQueueCapacity(), 1000));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setWaitForTasksToCompleteOnShutdown(true);
            executor.setAwaitTerminationSeconds(60);
            executor.setThreadNamePrefix(String.format("%s-task-", applicatonName));
            executor.setTaskDecorator(requestContextTaskDecorator());
        };
    }

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer(TaskSchedulingProperties properties, ErrorHandler errorHandler) {
        return scheduler -> {
            scheduler.setPoolSize(Math.min(properties.getPool().getSize(), 20));
            scheduler.setAwaitTerminationSeconds(60);
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
            scheduler.setThreadNamePrefix(String.format("%s-scheduling-", applicatonName));
            scheduler.setErrorHandler(errorHandler);
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorHandler defaultErrorHandler() {
        return new AsyncErrorHandler();
    }

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(ConfigurableListableBeanFactory beanFactory,
                                                                   TaskExecutor taskExecutor,
                                                                   ErrorHandler errorHandler) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster(beanFactory);
        multicaster.setTaskExecutor(taskExecutor);
        multicaster.setErrorHandler(errorHandler);
        return multicaster;
    }

    @ConditionalOnNotApplication(applicationNames = {"crypto-job-service"})
    @Bean
    public ThreadPoolMetricsCollector threadPoolMetricsCollector(ThreadPoolTaskExecutor taskExecutor,
                                                                 @Autowired(required = false) ThreadPoolTaskScheduler taskScheduler,
                                                                 MeterRegistry registry) {
        return new ThreadPoolMetricsCollector(taskExecutor, taskScheduler, registry);
    }
}