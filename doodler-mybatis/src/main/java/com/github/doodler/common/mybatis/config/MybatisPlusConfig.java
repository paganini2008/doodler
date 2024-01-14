package com.github.doodler.common.mybatis.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.doodler.common.mybatis.statistics.MyBatisMetricsCollector;
import com.github.doodler.common.mybatis.statistics.MyBatisStatisticsEndpoint;
import com.github.doodler.common.mybatis.statistics.MyBatisStatisticsService;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * 
 * @Description: MybatisPlusConfig
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
@Configuration
@MapperScan("com.yourproject.**.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }

    @ConditionalOnMissingBean
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return new DefaultConfigurationCustomizer();
    }
    
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }
    
    @Bean
    public MyBatisStatisticsService myBatisStatisticsService() {
    	return new MyBatisStatisticsService();
    }
    
    @Bean
    public MyBatisStatisticsEndpoint myBatisStatisticsEndpoint() {
    	return new MyBatisStatisticsEndpoint();
    }
    
    @Bean
    public MyBatisMetricsCollector myBatisMetricsCollector(MeterRegistry meterRegistry,MyBatisStatisticsService myBatisStatisticsService) {
    	return new MyBatisMetricsCollector(meterRegistry, myBatisStatisticsService);
    }

    static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
            setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        }
    }
}