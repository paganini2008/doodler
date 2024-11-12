package com.github.doodler.common.cloud.feign;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import com.github.doodler.common.cloud.lb.LoadBalancerClient;
import com.github.doodler.common.http.HttpComponentProperties;
import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: StandardClientFactory
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class StandardClientFactory implements FactoryBean<StandardClient> {

    private final HttpComponentProperties httpComponentProperties;
    private final LoadBalancerClient loadBalancerClient;
    private final BeanFactory beanFactory;

    @Override
    public StandardClient getObject() throws Exception {
        return new StandardClient(getActualClient(), loadBalancerClient, beanFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return StandardClient.class;
    }

    protected OkHttpClient getActualClient() {
        HttpComponentProperties.Okhttp config = httpComponentProperties.getOkhttp();
        okhttp3.OkHttpClient okHttpClient =
                new okhttp3.OkHttpClient.Builder().retryOnConnectionFailure(false)
                        .connectionPool(new okhttp3.ConnectionPool(config.getMaxIdleConnections(),
                                config.getKeepAliveDuration(), TimeUnit.SECONDS))
                        .connectTimeout(Duration.ofSeconds(config.getConnectionTimeout()))
                        .writeTimeout(Duration.ofSeconds(config.getWriteTimeout()))
                        .readTimeout(Duration.ofSeconds(config.getReadTimeout())).build();
        return new OkHttpClient(okHttpClient);
    }

}
