package com.github.doodler.common.jdbc.druid;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.doodler.common.context.ConditionalOnNotApplication;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: DruidConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022 13:15
 * @Version 1.0.0
 */
@ConditionalOnClass({DruidDataSource.class})
@Configuration(proxyBeanMethods = false)
public class DruidConfig {

    @ConditionalOnProperty("spring.datasource.druid.monitor.enabled")
    @Bean
    public ServletRegistrationBean<Servlet> druidServlet() {
        ServletRegistrationBean<Servlet> servletRegistrationBean =
                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("allow", "");
        servletRegistrationBean.addInitParameter("loginUsername", "druid");
        servletRegistrationBean.addInitParameter("loginPassword", "globalTLLC09");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @ConditionalOnProperty("spring.datasource.druid.monitor.enabled")
    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addInitParameter("exclusions",
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,");
        return filterRegistrationBean;
    }

    @ConditionalOnNotApplication(applicationNames = {"crypto-job-service"})
    @Bean
    public DruidMetricsCollector druidMetricsCollector(DataSource dataSource,
            MeterRegistry meterRegistry) {
        return new DruidMetricsCollector(dataSource, meterRegistry);
    }

    @ConditionalOnProperty(name = "management.health.druid.enabled", havingValue = "true",
            matchIfMissing = true)
    @Bean
    public DruidHealthIndicator druidHealthIndicator(DataSource dataSource) {
        return new DruidHealthIndicator(dataSource);
    }
}
