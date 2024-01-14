package com.github.doodler.common.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.context.FormattedMessageLocalization;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.context.ServerProperties;
import com.github.doodler.common.utils.JacksonUtils;
import com.github.doodler.common.utils.Markers;
import com.github.doodler.common.webmvc.WebServerConfig.WebRequestLoggingProperties;
import com.github.doodler.common.webmvc.undertow.UndertowMetricsHandlerWrapper;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Servlet;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @Description: WebServerConfig
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
@EnableConfigurationProperties({WebRequestLoggingProperties.class, ServerProperties.class})
@Configuration(proxyBeanMethods = false)
public class WebServerConfig {

    @Value("${spring.mvc.servlet.path}")
    private String servletContextPath;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public ServletRegistrationBean<Servlet> indexPage() {
        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>(
                new IndexPage(servletContextPath), "/"
        );
        return servletRegistrationBean;
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.getObjectMapperForWebMvc();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setMaxUploadSize(DataSize.ofMegabytes(100).toBytes());
        return multipartResolver;
    }

    @Profile({"dev", "test", "prod"})
    @Bean
    public WebRequestStdout webRequestStdout(WebRequestLoggingProperties webRequestLoggingProperties) {
        return new WebRequestStdout(webRequestLoggingProperties, globalMarker());
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
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer(
            UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return deploymentInfo ->
                deploymentInfo.addOuterHandlerChainWrapper(undertowMetricsHandlerWrapper);
    }

    /**
     * @Description: TraceChainConig
     * @Author: Fred Feng
     * @Date: 25/02/2023
     * @Version 1.0.0
     */
    @ConditionalOnProperty(name = "api.trace.chain.enabled", havingValue = "true", matchIfMissing = true)
    @Configuration(proxyBeanMethods = false)
    public static class TraceChainConig {

        @Bean
        public TraceableFilter traceableFilter() {
            return new TraceableFilter();
        }

        @Bean
        public WebResponsePreHandler traceableWebResponsePreHandler() {
            return new TraceableWebResponsePreHandler();
        }
    }

    @ConfigurationProperties("web.request.logging")
    public static class WebRequestLoggingProperties {

        @Setter
        @Getter
        private List<String> paths = new ArrayList<>();
    }
}