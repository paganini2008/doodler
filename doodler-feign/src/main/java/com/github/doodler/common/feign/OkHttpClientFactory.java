package com.github.doodler.common.feign;

import feign.okhttp.OkHttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Description: OkHttpClientFactory
 * @Author: Fred Feng
 * @Date: 20/11/2020
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OkHttpClientFactory implements FactoryBean<OkHttpClient> {

    private final RestClientProperties restClientProperties;

    @Override
    public OkHttpClient getObject() throws Exception {
        return new OkHttpClient(createDelegateClient());
    }

    private okhttp3.OkHttpClient createDelegateClient() {
        RestClientProperties.Okhttp config = restClientProperties.getOkhttp();
        return new okhttp3.OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(new okhttp3.ConnectionPool(config.getMaxIdleConnections(), config.getKeepAliveDuration(),
                        TimeUnit.SECONDS))
                .connectTimeout(Duration.ofSeconds(config.getConnectionTimeout()))
                .writeTimeout(Duration.ofSeconds(config.getWriteTimeout())).readTimeout(
                        Duration.ofSeconds(config.getReadTimeout()))
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return OkHttpClient.class;
    }
}