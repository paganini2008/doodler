package com.github.doodler.common.http;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.retry.RetryTemplateCustomizer;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * @Description: RestTemplateConfig
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
@EnableConfigurationProperties({HttpComponentProperties.class})
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @ConditionalOnMissingBean(name = "defaultRestTemplateCustomizer")
    @Bean
    public DefaultRestTemplateCustomizer defaultRestTemplateCustomizer(
            ClientHttpRequestFactory clientHttpRequestFactory, RetryTemplate retryTemplate) {
        return new DefaultRestTemplateCustomizer(clientHttpRequestFactory,
                Arrays.asList(new LoggingHttpRequestInterceptor()), retryTemplate);
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpComponentProperties httpConfig) {
        OkHttpClient.Builder okHttpClientBuilder =
                new OkHttpClient.Builder().retryOnConnectionFailure(false).followRedirects(true)
                        .connectionPool(new okhttp3.ConnectionPool(
                                httpConfig.getOkhttp().getMaxIdleConnections(),
                                httpConfig.getOkhttp().getKeepAliveDuration(), TimeUnit.SECONDS))
                        .connectTimeout(
                                Duration.ofSeconds(httpConfig.getOkhttp().getConnectionTimeout()))
                        .readTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getReadTimeout()))
                        .writeTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getWriteTimeout()));
        if (httpConfig.getProxy() != null && StringUtils.isNotBlank(httpConfig.getProxy().getHost())
                && httpConfig.getProxy().getPort() > 0) {
            HttpComponentProperties.Proxy proxyConfig = httpConfig.getProxy();
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort()));
            okHttpClientBuilder.proxy(proxy);
            if (StringUtils.isNotBlank(proxyConfig.getUsername())
                    && StringUtils.isNotBlank(proxyConfig.getPassword())) {
                Authenticator proxyAuthenticator = new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) {
                        String credentials = Credentials.basic(proxyConfig.getUsername(),
                                proxyConfig.getPassword());
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credentials).build();
                    }
                };
                okHttpClientBuilder.proxyAuthenticator(proxyAuthenticator);
            }
        }
        return new OkHttp3ClientHttpRequestFactory(okHttpClientBuilder.build());
    }

    @Bean
    public RestTemplateHolder restTemplateHolder(
            @Qualifier("defaultRestTemplateCustomizer") RestTemplateCustomizer customizer) {
        return new RestTemplateHolder(customizer);
    }

    @ConditionalOnMissingBean
    @Bean
    public RestTemplate defaultRestTemplate(RestTemplateHolder restTemplateHolder) {
        return restTemplateHolder.getRestTemplate();
    }

    @ConditionalOnMissingBean
    @Bean
    public RetryTemplate defaultRetryTemplate(
            @Autowired(required = false) RetryTemplateCustomizer retryTemplateCustomizer) {
        RetryTemplate retryTemplate = RetryTemplateUtils.getRetryTemplate(3);
        if (retryTemplateCustomizer != null) {
            retryTemplateCustomizer.customize(retryTemplate);
        }
        return retryTemplate;
    }

}
