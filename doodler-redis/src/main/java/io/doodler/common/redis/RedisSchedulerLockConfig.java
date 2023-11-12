package io.doodler.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * @Description: RedisSchedulerLockConfig
 * @Author: Fred Feng
 * @Date: 09/02/2023
 * @Version 1.0.0
 */
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@Configuration(proxyBeanMethods = false)
public class RedisSchedulerLockConfig {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${spring.application.name}")
    private String applicationName;

    @ConditionalOnMissingBean
    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, env, String.format("shedlock:%s", applicationName));
    }
}