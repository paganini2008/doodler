package io.doodler.common.mybatis.config;

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

import io.doodler.common.mybatis.statistics.MyBatisMetricsCollector;
import io.doodler.common.mybatis.statistics.MyBatisStatisticsEndpoint;
import io.doodler.common.mybatis.statistics.MyBatisStatisticsService;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@MapperScan("io.doodler.**.mapper")
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

        /**
         * 插入元对象字段填充（用于插入时对公共字段的填充）
         *
         * @param metaObject 元对象
         */
        @Override
        public void insertFill(MetaObject metaObject) {
            setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
            setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        }

        /**
         * 更新元对象字段填充（用于更新时对公共字段的填充）
         *
         * @param metaObject 元对象
         */
        @Override
        public void updateFill(MetaObject metaObject) {
            setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        }
    }
}