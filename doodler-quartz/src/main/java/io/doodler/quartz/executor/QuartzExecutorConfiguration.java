package io.doodler.quartz.executor;

import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.doodler.common.retry.RetryOperations;
import io.doodler.discovery.ApplicationInfoHolder;
import io.doodler.discovery.DiscoveryClientService;
import io.doodler.discovery.LoadBalancedRestTemplate;
import io.doodler.quartz.scheduler.JobOperations;

/**
 * @Description: QuartzExecutorConfiguration
 * @Author: Fred Feng
 * @Date: 21/06/2023
 * @Version 1.0.0
 */
@Import({JobStartController.class})
@Configuration(proxyBeanMethods = false)
public class QuartzExecutorConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public JobOperations jobOperations(LoadBalancedRestTemplate restTemplate) {
        return new RestJobOperations(restTemplate);
    }

    @Bean
    public JobExecutionBeanProcessor jobExecutionBeanProcessor(JobOperations jobOperations,
    		                                                   RetryOperations retryOperations,
    		                                                   @Lazy ThreadPoolTaskExecutor taskExecutor,
    		                                                   Marker marker) {
        return new JobExecutionBeanProcessor(jobOperations, retryOperations, taskExecutor, marker);
    }

    @ConditionalOnMissingBean
    @Bean
    public JobBeanFactory jobBeanFactory() {
        return new DefaultJobBeanFactory();
    }

    @Bean
    public JobService jobService(JobBeanFactory jobBeanFactory,
                                 ApplicationInfoHolder applicationInfoHolder,
                                 LoadBalancedRestTemplate restTemplate) {
        return new JobService(jobBeanFactory, applicationInfoHolder, restTemplate);
    }

    @Bean
    public SimpleJobTemplate simpleJobTemplate(JobOperations jobOperations, Marker marker) {
        SimpleJobTemplate jobTemplate = new SimpleJobTemplate(jobOperations, marker);
        jobTemplate.setDefaultJobGroupName(applicationName.toUpperCase());
        jobTemplate.setDefaultTriggerGroupName(String.format("%s-TRIGGER-GROUP", applicationName.toUpperCase()));
        return jobTemplate;
    }
    
    @ConditionalOnProperty(name = "management.health.quartz.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public QuartzExecutorHealthIndicator quartzHealthIndicator(DiscoveryClientService discoveryClientService) {
    	return new QuartzExecutorHealthIndicator(discoveryClientService);
    }
}