package io.doodler.common.cache;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import io.doodler.common.cache.feign.CacheableInitializingRestClientBean;
import io.doodler.common.cache.feign.RestClientKeyGenerator;
import io.doodler.common.cache.filter.CacheMethodFilter;
import io.doodler.common.cache.filter.CacheMethodFilterChain;
import io.doodler.common.cache.filter.CacheSynchronizationFilter;
import io.doodler.common.cache.multilevel.MultiLevelCacheKeyRemovalListener;
import io.doodler.common.cache.multilevel.MultiLevelCacheManager;
import io.doodler.common.cache.multilevel.MultiLevelCacheStatisticsEndpoint;
import io.doodler.common.cache.multilevel.NoopMultiLevelCacheKeyRemovalListener;
import io.doodler.common.cache.redis.EnhancedRedisCacheManager;
import io.doodler.common.cache.redis.RedisCacheConfigUtils;
import io.doodler.common.cache.redis.RedisCacheConfigurationHolder;
import io.doodler.common.cache.redis.RedisCacheKeyRemovalListenerContainer;
import io.doodler.common.cache.redis.RedisCacheLoader;
import io.doodler.common.cache.redis.RedisCacheStatisticsEndpoint;
import io.doodler.common.cache.redis.RedisCacheStatisticsMetricsCollector;
import io.doodler.common.cache.spec.CacheSpecifications;
import io.doodler.common.cache.spec.CacheSpecificationsBeanPostProcessor;
import io.doodler.common.cache.statistics.CacheStatisticsFilter;
import io.doodler.common.cache.statistics.CacheStatisticsService;
import io.doodler.common.context.InstanceId;
import io.doodler.common.feign.RestClientCandidatesAutoConfiguration;
import io.doodler.common.redis.RedisConfig;
import io.doodler.common.redis.pubsub.RedisPubSubConfig;
import io.doodler.common.redis.pubsub.RedisPubSubService;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: GenericCacheConfig
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({RestClientCandidatesAutoConfiguration.class, RedisConfig.class, RedisPubSubConfig.class})
@EnableConfigurationProperties({CacheExtensionProperties.class})
@Import({CacheManagerEndpoint.class})
@Configuration(proxyBeanMethods = false)
public class GenericCacheConfig {

    @Bean("genericKeyGenerator")
    public KeyGenerator genericKeyGenerator() {
        return new GenericKeyGenerator();
    }

    @Bean("restClientKeyGenerator")
    public KeyGenerator restClientKeyGenerator() {
        return new RestClientKeyGenerator();
    }

    @Bean
    public CacheControl cacheControl() {
        return new CacheControl();
    }

    @Bean
    public RedisCacheConfigurationHolder redisCacheConfigurationHolder(CacheExtensionProperties config,
                                                                       CacheSpecifications cacheSpecifications,
                                                                       RedisCacheLoader redisCacheLoader) {
        return new RedisCacheConfigurationHolder(config, cacheSpecifications, redisCacheLoader);
    }

    @Bean
    public CacheSpecifications cacheSpecifications() {
        return new CacheSpecifications();
    }

    @Bean
    public CacheLifeCycleExtension cacheLifeCycleExtension(@Lazy CacheManager cacheManager) {
        return new CacheLifeCycleExtension(cacheManager);
    }

    @Bean
    public CacheSpecificationsBeanPostProcessor cacheSpecificationsBeanPostProcessor() {
        return new CacheSpecificationsBeanPostProcessor();
    }

    @Bean
    public RedisCacheLoader redisCacheLoader(RedisConnectionFactory redisConnectionFactory,
                                             CacheSpecifications cacheSpecifications) {
        return new RedisCacheLoader(redisConnectionFactory, cacheSpecifications);
    }

    @DependsOn("redisMessageEventDispatcher")
    @Bean
    public CacheChangeEventHandler cacheChangeEventHandler(InstanceId instanceId,
                                                           CacheExtensionProperties cacheExtensionProperties,
                                                           CacheManager cacheManager) {
        return new CacheChangeEventHandler(instanceId, cacheExtensionProperties, cacheManager);
    }

    @Bean
    public RedisCacheKeyRemovalListenerContainer redisCacheKeyRemovalListenerContainer() {
        return new RedisCacheKeyRemovalListenerContainer();
    }

    @Bean
    public CacheableInitializingRestClientBean cacheableInitializingRestClientBean(
            CacheSpecifications cacheSpecifications, CacheLifeCycleExtension cacheLifeCycleExtension) {
        return new CacheableInitializingRestClientBean(cacheSpecifications, cacheLifeCycleExtension);
    }

    @ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "redis", matchIfMissing = true)
    @Import({RedisCacheStatisticsEndpoint.class})
    @Configuration(proxyBeanMethods = false)
    public class RedisCacheConfig {

        @Value("${spring.application.name}")
        private String applicationName;

        @Bean
        public CacheStatisticsService cacheStatisticsService() {
            return new CacheStatisticsService();
        }

        @Bean
        public CacheMethodFilter cacheMethodFilter(InstanceId instanceId,
                                                   RedisPubSubService redisPubSubService,
                                                   CacheStatisticsService cacheStatisticsService,
                                                   CacheSpecifications cacheSpecifications) {
            return new CacheMethodFilterChain(
                    new CacheSynchronizationFilter(applicationName, instanceId, redisPubSubService))
                    .andThen(new CacheStatisticsFilter(applicationName, cacheStatisticsService, cacheSpecifications));
        }

        @Bean
        public CacheManager cacheManager(
                RedisConnectionFactory redisConnectionFactory,
                RedisCacheConfigurationHolder redisCacheConfigurationHolder,
                CacheSpecifications cacheSpecifications,
                CacheMethodFilter cacheMethodFilter,
                CacheControl cacheControl) {
            RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfigUtils
                    .getDefaultCacheConfiguration(applicationName);
            return new EnhancedRedisCacheManager(redisConnectionFactory,
                    RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                    defaultCacheConfiguration,
                    Collections.emptyMap(),
                    redisCacheConfigurationHolder,
                    cacheSpecifications,
                    cacheMethodFilter,
                    cacheControl);
        }

        @Bean
        public RedisCacheStatisticsMetricsCollector redisCacheStatisticsMetricsCollector(MeterRegistry registry,
                                                                                         CacheStatisticsService cacheStatisticsService,
                                                                                         CacheSpecifications cacheSpecifications) {
            return new RedisCacheStatisticsMetricsCollector(registry, cacheStatisticsService, cacheSpecifications);
        }
    }

    @ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "multilevel")
    @Import({MultiLevelCacheStatisticsEndpoint.class})
    @Configuration(proxyBeanMethods = false)
    public class MultiLevelCacheConfig {

        @Value("${spring.application.name}")
        private String applicationName;

        @ConditionalOnMissingBean
        @Bean
        public MultiLevelCacheKeyRemovalListener multiLevelCacheKeyRemovalListener() {
            return new NoopMultiLevelCacheKeyRemovalListener();
        }

        @Autowired
        public void addCacheKeyRemovalListener(RedisCacheKeyRemovalListenerContainer redisCacheKeyRemovalHandler,
                                               MultiLevelCacheKeyRemovalListener multiLevelCacheKeyRemovalListener) {
            redisCacheKeyRemovalHandler.addCacheKeyRemovalListener((cacheName, cacheKey, cacheValue) -> {
                multiLevelCacheKeyRemovalListener.onRemovalRemoteCacheKey(cacheName, cacheKey, cacheValue);
            });
        }

        @Bean("localCacheStatisticsService")
        public CacheStatisticsService localCacheStatisticsService() {
            return new CacheStatisticsService();
        }

        @Bean("localCacheMethodFilter")
        public CacheMethodFilter localCacheMethodFilter(
                @Qualifier("localCacheStatisticsService") CacheStatisticsService cacheStatisticsService,
                CacheSpecifications cacheSpecifications) {
            return new CacheStatisticsFilter(applicationName, cacheStatisticsService, cacheSpecifications);
        }

        @Bean("remoteCacheStatisticsService")
        public CacheStatisticsService remoteCacheStatisticsService() {
            return new CacheStatisticsService();
        }

        @Bean("remoteCacheMethodFilter")
        public CacheMethodFilter remoteCacheMethodFilter(
                @Qualifier("remoteCacheStatisticsService") CacheStatisticsService cacheStatisticsService,
                CacheSpecifications cacheSpecifications) {
            return new CacheStatisticsFilter(applicationName, cacheStatisticsService, cacheSpecifications);
        }

        @Bean("multiLevelCacheMethodFilter")
        public CacheMethodFilter multiLevelCacheMethodFilter(InstanceId instanceId, RedisPubSubService redisPubSubService) {
            return new CacheSynchronizationFilter(applicationName, instanceId, redisPubSubService);
        }

        @Bean
        public CacheManager cacheManager(
                RedisCacheConfigurationHolder redisCacheConfigurationHolder,
                CacheSpecifications cacheSpecifications,
                RedisConnectionFactory redisConnectionFactory,
                @Qualifier("localCacheMethodFilter") CacheMethodFilter localCacheMethodFilter,
                @Qualifier("remoteCacheMethodFilter") CacheMethodFilter remoteCacheMethodFilter,
                @Qualifier("multiLevelCacheMethodFilter") CacheMethodFilter multiLevelCacheMethodFilter,
                CacheControl cacheControl,
                MultiLevelCacheKeyRemovalListener multiLevelCacheKeyRemovalListener) {

            DefaultLocalCacheManager caffeineCacheManager = new DefaultLocalCacheManager(cacheSpecifications,
                    localCacheMethodFilter, cacheControl);
            caffeineCacheManager.addCacheKeyRemovalListener((cacheName, cacheKey, cacheValue) -> {
                multiLevelCacheKeyRemovalListener.onRemovalLocalCacheKey(cacheName, cacheKey, cacheValue);
            });
            RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfigUtils
                    .getDefaultCacheConfiguration(applicationName);

            RedisCacheManager redisCacheManager = new EnhancedRedisCacheManager(redisConnectionFactory,
                    RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                    defaultCacheConfiguration,
                    Collections.emptyMap(),
                    redisCacheConfigurationHolder,
                    cacheSpecifications,
                    remoteCacheMethodFilter,
                    cacheControl);
            redisCacheManager.afterPropertiesSet();

            return new MultiLevelCacheManager(caffeineCacheManager, redisCacheManager,
                    cacheSpecifications,
                    multiLevelCacheMethodFilter);
        }
    }
}