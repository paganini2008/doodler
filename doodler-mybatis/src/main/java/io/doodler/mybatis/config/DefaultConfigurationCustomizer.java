package io.doodler.mybatis.config;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;

import io.doodler.mybatis.SimpleEnumTypeHandler;
import io.doodler.mybatis.utils.SqlTraceInterceptor;
import io.doodler.mybatis.utils.TimestampLocalDateTimeTypeHandler;

/**
 * @Description: DefaultConfigurationCustomizer
 * @Author: Fred Feng
 * @Date: 06/01/2023
 * @Version 1.0.0
 */
public class DefaultConfigurationCustomizer implements ConfigurationCustomizer {
	
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
    	interceptors.add(new SqlTraceInterceptor(marker));
    }
}