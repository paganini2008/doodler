package io.doodler.cache;

import io.doodler.cache.feign.CacheableInitializingRestClientBean;
import io.doodler.cache.feign.RestClientKeyGenerator;
import io.doodler.cache.filter.CacheHitStatisticalFilter;
import io.doodler.cache.filter.CacheMethodFilter;
import io.doodler.cache.filter.CacheMethodFilterChain;
import io.doodler.cache.filter.CacheSynchronizationFilter;
import io.doodler.cache.multilevel.MultiLevelCacheHitEndpoint;
import io.doodler.cache.multilevel.MultiLevelCacheKeyRemovalListener;
import io.doodler.cache.multilevel.MultiLevelCacheManager;
import io.doodler.cache.multilevel.NoopMultiLevelCacheKeyRemovalListener;
import io.doodler.cache.redis.EnhancedRedisCacheManager;
import io.doodler.cache.redis.RedisCacheConfigUtils;
import io.doodler.cache.redis.RedisCacheConfigurationHolder;
import io.doodler.cache.redis.RedisCacheHitEndpoint;
import io.doodler.cache.redis.RedisCacheKeyRemovalListenerContainer;
import io.doodler.cache.redis.RedisCacheLoader;
import io.doodler.cache.spec.CacheSpecifications;
import io.doodler.cache.spec.CacheSpecificationsBeanPostProcessor;
import io.doodler.common.context.InstanceId;
import io.doodler.feign.RestClientCandidatesAutoConfiguration;
import io.doodler.redis.RedisConfig;
import io.doodler.redis.pubsub.RedisPubSubConfig;
import io.doodler.redis.pubsub.RedisPubSubService;

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
    @Import({RedisCacheHitEndpoint.class})
    @Configuration(proxyBeanMethods = false)
    public class RedisCacheConfig {

        @Value("${spring.application.name}")
        private String applicationName;

        @Bean
        public CacheHitStatisticsCollector cacheHitStatisticsCollector() {
            return new CacheHitStatisticsCollector();
        }

        @Bean
        public CacheMethodFilter cacheMethodFilter(InstanceId instanceId, RedisPubSubService redisPubSubService,
                                                   CacheHitStatisticsCollector cacheHitStatisticsCollector) {
            return new CacheMethodFilterChain(
                    new CacheSynchronizationFilter(applicationName, instanceId, redisPubSubService))
                    .andThen(new CacheHitStatisticalFilter(cacheHitStatisticsCollector));
        }

        @Bean
        public CacheManager cacheManager(
                RedisConnectionFactory redisConnectionFactory,
                RedisCacheConfigurationHolder redisCacheConfigurationHolder,
                CacheSpecifications cacheSpecifications,
                CacheMethodFilter cacheMethodFilter) {
            RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfigUtils
                    .getDefaultCacheConfiguration(applicationName);
            return new EnhancedRedisCacheManager(redisConnectionFactory,
                    RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                    defaultCacheConfiguration,
                    Collections.emptyMap(),
                    redisCacheConfigurationHolder,
                    cacheSpecifications,
                    cacheMethodFilter);
        }
    }

    @ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "multilevel")
    @Import({MultiLevelCacheHitEndpoint.class})
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

        @Bean("localCacheHitStatisticsCollector")
        public CacheHitStatisticsCollector localCacheHitStatisticsCollector() {
            return new CacheHitStatisticsCollector();
        }

        @Bean("localCacheMethodFilter")
        public CacheMethodFilter localCacheMethodFilter(
                @Qualifier("localCacheHitStatisticsCollector") CacheHitStatisticsCollector localHitStatisticsCollector) {
            return new CacheHitStatisticalFilter(localHitStatisticsCollector);
        }

        @Bean("remoteCacheHitStatisticsCollector")
        public CacheHitStatisticsCollector remoteCacheHitStatisticsCollector() {
            return new CacheHitStatisticsCollector();
        }

        @Bean("remoteCacheMethodFilter")
        public CacheMethodFilter remoteCacheMethodFilter(
                @Qualifier("remoteCacheHitStatisticsCollector") CacheHitStatisticsCollector remoteCacheHitStatisticsCollector) {
            return new CacheHitStatisticalFilter(remoteCacheHitStatisticsCollector);
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
                MultiLevelCacheKeyRemovalListener multiLevelCacheKeyRemovalListener) {

            DefaultLocalCacheManager caffeineCacheManager = new DefaultLocalCacheManager(cacheSpecifications,
                    localCacheMethodFilter);
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
                    remoteCacheMethodFilter);
            redisCacheManager.afterPropertiesSet();

            return new MultiLevelCacheManager(caffeineCacheManager, redisCacheManager,
                    cacheSpecifications,
                    multiLevelCacheMethodFilter);
        }
    }
}