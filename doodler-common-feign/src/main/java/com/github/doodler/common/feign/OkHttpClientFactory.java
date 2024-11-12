package com.github.doodler.common.feign;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.FactoryBean;
import com.github.doodler.common.http.HttpComponentProperties;
import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: OkHttpClientFactory
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OkHttpClientFactory implements FactoryBean<OkHttpClient> {

    private final HttpComponentProperties httpComponentProperties;

    @Override
    public OkHttpClient getObject() throws Exception {
        return new OkHttpClient(createDelegateClient());
    }

    private okhttp3.OkHttpClient createDelegateClient() {
        HttpComponentProperties.Okhttp config = httpComponentProperties.getOkhttp();
        return new okhttp3.OkHttpClient.Builder().retryOnConnectionFailure(false)
                .connectionPool(new okhttp3.ConnectionPool(config.getMaxIdleConnections(),
                        config.getKeepAliveDuration(), TimeUnit.SECONDS))
                .connectTimeout(Duration.ofSeconds(config.getConnectionTimeout()))
                .writeTimeout(Duration.ofSeconds(config.getWriteTimeout()))
                .readTimeout(Duration.ofSeconds(config.getReadTimeout())).build();
    }

    @Override
    public Class<?> getObjectType() {
        return OkHttpClient.class;
    }
}
