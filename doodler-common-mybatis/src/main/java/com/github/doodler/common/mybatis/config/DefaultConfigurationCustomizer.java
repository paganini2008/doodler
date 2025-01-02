package com.github.doodler.common.mybatis.config;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.github.doodler.common.mybatis.SimpleEnumTypeHandler;
import com.github.doodler.common.mybatis.statistics.MyBatisStatisticsService;
import com.github.doodler.common.mybatis.statistics.SqlTraceInterceptor;
import com.github.doodler.common.mybatis.utils.TimestampLocalDateTimeTypeHandler;
import lombok.RequiredArgsConstructor;

/**
 * @Description: DefaultConfigurationCustomizer
 * @Author: Fred Feng
 * @Date: 06/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultConfigurationCustomizer implements ConfigurationCustomizer {

    @Autowired
    private MyBatisStatisticsService myBatisStatisticsService;

    @Autowired
    private Marker marker;

    @Override
    public void customize(MybatisConfiguration configuration) {
        configuration.setDefaultEnumTypeHandler(SimpleEnumTypeHandler.class);
        registerTypeHandler(configuration.getTypeHandlerRegistry());
        List<Interceptor> interceptors = new ArrayList<>();
        addInterceptor(interceptors);
        for (Interceptor interceptor : interceptors) {
            configuration.addInterceptor(interceptor);
        }
    }

    protected void registerTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        typeHandlerRegistry.register(Timestamp.class, TimestampLocalDateTimeTypeHandler.class);
    }

    protected void addInterceptor(List<Interceptor> interceptors) {
        interceptors.add(new SqlTraceInterceptor(myBatisStatisticsService, marker));
    }
}
