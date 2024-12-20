package com.github.doodler.common.gateway;

import java.util.function.Predicate;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 
 * @Description: GatewayConfig
 * @Author: Fred Feng
 * @Date: 03/11/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfig {

    @Bean("testPredicate")
    public Predicate<Object> testPredicate() {
        return obj -> true;
    }

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator(
            GatewayProperties properties) {
        return new PropertiesRouteDefinitionLocator(properties);
    }

}
