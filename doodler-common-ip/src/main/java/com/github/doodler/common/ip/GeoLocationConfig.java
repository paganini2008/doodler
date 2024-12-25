package com.github.doodler.common.ip;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.context.ConditionalOnApplication;
import com.github.doodler.common.http.RestTemplateHolder;

/**
 * 
 * @Description: GeoLocationConfig
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
@ConditionalOnApplication("doodler-common-service")
@Configuration(proxyBeanMethods = false)
public class GeoLocationConfig {

    @ConditionalOnMissingBean
    @Bean
    public GeoLocationService geoLocationService(RestTemplateHolder restTemplateHolder) {
        return new DefaultGeoLocationService(restTemplateHolder);
    }

}
