package io.doodler.webmvc;

import javax.servlet.Servlet;

import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.doodler.common.context.ConditionalOnNotApplication;
import io.doodler.common.context.FormattedMessageLocalization;
import io.doodler.common.context.MessageLocalization;
import io.doodler.common.utils.JacksonUtils;
import io.doodler.common.utils.Markers;
import io.doodler.i18n.I18nMessageLocalization;
import io.doodler.i18n.IRemoteI18nService;

/**
 * @Description: WebServerConfig
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
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
        return JacksonUtils.getGenericObjectMapper();
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
    
    @Profile({"dev","test","prod"})
    @Bean
    public WebRequestStdout webRequestStdout() {
    	return new WebRequestStdout(globalMarker());
    }
    
    @Bean
    public Marker globalMarker() {
    	return Markers.forName(applicationName);
    }

    /**
     * @Description: I18nMessageConfig
     * @Author: Fred Feng
     * @Date: 31/12/2022
     * @Version 1.0.0
     */
    @ConditionalOnNotApplication(applicationNames = {"crypto-common-service"})
    @Configuration(proxyBeanMethods = false)
    public static class I18nMessageConfig {

        @ConditionalOnMissingClass("io.doodler.i18n.IRemoteI18nService")
        @Bean
        public MessageLocalization messageLocalization() {
            return new FormattedMessageLocalization();
        }

        @ConditionalOnClass(IRemoteI18nService.class)
        @Bean
        public MessageLocalization i18nMessageLocalization(IRemoteI18nService remoteI18nService) {
            return new I18nMessageLocalization(remoteI18nService);
        }
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
}