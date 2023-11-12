package io.doodler.common.quartz.scheduler;

import java.io.IOException;
import java.util.Properties;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.doodler.common.quartz.executor.QuartzExecutorConfiguration;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: QuartzAutoConfiguration
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({QuartzExecutorConfiguration.class, DataSourceAutoConfiguration.class})
@ComponentScan("com.elraytech.maxibet.common.quartz.scheduler")
@Configuration(proxyBeanMethods = false)
public class QuartzSchedulerConfiguration {

    private static final String QUARTZ_PROPERTIES_PATH = "quartz-%s.properties";

    @Value("${spring.profiles.active}")
    private String env;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean(destroyMethod = "destroy")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory,
                                                     PlatformTransactionManager transactionManager) throws Exception {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setStartupDelay(10);
        factory.setJobFactory(jobFactory);
        factory.setTransactionManager(transactionManager);
        factory.setQuartzProperties(quartzProperties());
        factory.setWaitForJobsToCompleteOnShutdown(true);
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(String.format(QUARTZ_PROPERTIES_PATH, env)));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public JobLogService defaultJobLogService(Marker marker, ObjectMapper objectMapper) {
        return new Slf4jJobLogService(marker, objectMapper);
    }

    @Bean
    public JobSchedulerMeterCounter jobSchedulerMeterCounter(MeterRegistry meterRegistry) {
        return new JobSchedulerMeterCounter(meterRegistry);
    }

    /**
     * @Description: AutowiringSpringBeanJobFactory
     * @Author: Fred Feng
     * @Date: 15/06/2023
     * @Version 1.0.0
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private AutowireCapableBeanFactory beanFactory;

        AutowiringSpringBeanJobFactory() {
        }

        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
    
    @Bean
    public Counters counters() {
    	return new Counters();
    }

    @ConditionalOnProperty(name = "management.health.quartz.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public QuartzSchedulerHealthIndicator quartzHealthIndicator(JobManager jobManager) {
        return new QuartzSchedulerHealthIndicator(jobManager);
    }
}