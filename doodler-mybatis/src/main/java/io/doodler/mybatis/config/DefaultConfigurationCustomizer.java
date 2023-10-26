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

import io.doodler.common.enums.BonusType;
import io.doodler.common.enums.PaymentSource;
import io.doodler.common.enums.PaymentStatus;
import io.doodler.common.enums.TransType;
import io.doodler.mybatis.SimpleEnumTypeHandler;
import io.doodler.mybatis.statistics.MyBatisStatisticsService;
import io.doodler.mybatis.statistics.SqlTraceInterceptor;
import io.doodler.mybatis.utils.TimestampLocalDateTimeTypeHandler;
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
        typeHandlerRegistry.register(BonusType.class, new SimpleEnumTypeHandler<>(BonusType.class));
        typeHandlerRegistry.register(PaymentSource.class, new SimpleEnumTypeHandler<>(PaymentSource.class));
        typeHandlerRegistry.register(PaymentStatus.class, new SimpleEnumTypeHandler<>(PaymentStatus.class));
        typeHandlerRegistry.register(TransType.class, new SimpleEnumTypeHandler<>(TransType.class));
    }

    protected void addInterceptor(List<Interceptor> interceptors) {
    	interceptors.add(new SqlTraceInterceptor(myBatisStatisticsService, marker));
    }
}