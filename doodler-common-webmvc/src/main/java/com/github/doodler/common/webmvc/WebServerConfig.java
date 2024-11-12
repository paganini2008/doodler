package com.github.doodler.common.webmvc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Servlet;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.github.doodler.common.context.ContextPath;
import com.github.doodler.common.context.FormattedMessageLocalization;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.context.ServerProperties;
import com.github.doodler.common.utils.GlobalJackson2ObjectMapperBuilderCustomizer;
import com.github.doodler.common.utils.Markers;
import com.github.doodler.common.webmvc.WebServerConfig.WebRequestLoggerProperties;
import com.github.doodler.common.webmvc.undertow.UndertowMetricsHandlerWrapper;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: WebServerConfig
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
@EnableConfigurationProperties({WebRequestLoggerProperties.class, ServerProperties.class})
@Configuration(proxyBeanMethods = false)
public class WebServerConfig {

    @Autowired
    private ContextPath contextPath;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public ServletRegistrationBean<Servlet> indexPage() {
        ServletRegistrationBean<Servlet> servletRegistrationBean =
                new ServletRegistrationBean<>(new IndexPage(contextPath.getContextPath()), "/");
        return servletRegistrationBean;
    }

    @Primary
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new GlobalJackson2ObjectMapperBuilderCustomizer();
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

    @Profile({"dev", "test", "prod"})
    @Bean
    public WebRequestStdout webRequestStdout(WebRequestLoggerProperties loggerProperties) {
        return new WebRequestStdout(contextPath, loggerProperties, globalMarker());
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
        return deploymentInfo -> deploymentInfo
                .addOuterHandlerChainWrapper(undertowMetricsHandlerWrapper);
    }

    /**
     * @Description: TraceChainConig
     * @Author: Fred Feng
     * @Date: 25/02/2023
     * @Version 1.0.0
     */
    @ConditionalOnProperty(name = "api.trace.chain.enabled", havingValue = "true",
            matchIfMissing = true)
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
    public static class WebRequestLoggerProperties {

        @Setter
        @Getter
        private List<String> paths = new ArrayList<>();
    }
}
