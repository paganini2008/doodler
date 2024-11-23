package com.github.doodler.common.webflux;

import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.util.pattern.PathPatternParser;

import com.github.doodler.common.context.FormattedMessageLocalization;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.utils.Markers;

/**
 * 
 * @Description: BasicWebFluxConfig
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@ComponentScan("com.github.doodler.common.webflux")
@AutoConfigureBefore({WebFluxAutoConfiguration.class, ErrorWebFluxAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
public class BasicWebFluxConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @ConditionalOnProperty("spring.application.cors.enabled")
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public Marker globalMarker() {
        return Markers.forName(applicationName);
    }

    @ConditionalOnMissingBean
    @Bean
    public MessageLocalization messageLocalization() {
        return new FormattedMessageLocalization();
    }

    @Bean
    public WebRequestPostHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer,
                                                 RequestedContentTypeResolver requestedContentTypeResolver) {
        return new WebRequestPostHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

}
