package io.doodler.common.quartz.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

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
import io.doodler.common.quartz.statistics.CountingJobSchedulingListener;
import io.doodler.common.quartz.statistics.CountingStatisticsService;
import io.doodler.common.utils.MapUtils;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: QuartzAutoConfiguration
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({QuartzExecutorConfiguration.class, DataSourceAutoConfiguration.class})
@ComponentScan("io.doodler.common.quartz.scheduler")
@Configuration(proxyBeanMethods = false)
public class QuartzSchedulerConfiguration {
    private static final String DEFAULT_QUARTZ_PROPERTIES_NAME = "quartz.properties";

    private static final String QUARTZ_PROPERTIES_NAME = "quartz-%s.properties";

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
        ClassPathResource classPathResource = new ClassPathResource(DEFAULT_QUARTZ_PROPERTIES_NAME);
        Properties config = new Properties();
        if (classPathResource.exists()) {
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            propertiesFactoryBean.setLocation(classPathResource);
            propertiesFactoryBean.afterPropertiesSet();
            config.putAll(propertiesFactoryBean.getObject());
        }
        classPathResource = new ClassPathResource(String.format(QUARTZ_PROPERTIES_NAME, env));
        if (classPathResource.exists()) {
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            propertiesFactoryBean.setLocation(classPathResource);
            propertiesFactoryBean.afterPropertiesSet();
            config.putAll(propertiesFactoryBean.getObject());
        }
        return config;
    }

    @Bean
    public JobLogService defaultJobLogService(Marker marker, ObjectMapper objectMapper) {
        return new Slf4jJobLogService(marker, objectMapper);
    }

    @Bean
    public JobSchedulerStatusNotifier jobSchedulerStatusNotifier(MeterRegistry meterRegistry) {
        return new JobSchedulerStatusNotifier(meterRegistry);
    }

    @Bean
    public JobSchedulingListener counterMetricsJobSchedulingListener(MeterRegistry meterRegistry) {
        return new CounterMetricsJobSchedulingListener(meterRegistry);
    }

    /**
     * @Description: AutowiringSpringBeanJobFactory
     * @Author: Fred Feng
     * @Date: 15/06/2023
     * @Version 1.0.0
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private ApplicationContext applicationContext;
        private AutowireCapableBeanFactory beanFactory;

        AutowiringSpringBeanJobFactory() {
        }

        public void setApplicationContext(ApplicationContext context) {
            this.applicationContext = context;
            this.beanFactory = context.getAutowireCapableBeanFactory();
        }

        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            if (job instanceof JobSchedulingListenerAware) {
                Map<String, JobSchedulingListener> beanMap = applicationContext.getBeansOfType(JobSchedulingListener.class);
                if (MapUtils.isNotEmpty(beanMap)) {
                    List<JobSchedulingListener> listeners = new CopyOnWriteArrayList<>(beanMap.values());
                    ((JobSchedulingListenerAware) job).setJobSchedulingListeners(listeners);
                }
            }
            return job;
        }
    }

    @Bean
    public CountingStatisticsService countingStatisticsService() {
        return new CountingStatisticsService();
    }

    @Bean
    public JobSchedulingListener countingJobSchedulingListener(CountingStatisticsService countingStatisticsService) {
        return new CountingJobSchedulingListener(countingStatisticsService);
    }

    @ConditionalOnProperty(name = "management.health.quartz.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public QuartzSchedulerHealthIndicator quartzHealthIndicator(JobManager jobManager) {
        return new QuartzSchedulerHealthIndicator(jobManager);
    }
}