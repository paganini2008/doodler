package com.github.doodler.common.ws;

import org.slf4j.Marker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.context.ConditionalOnApplication;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.context.MetricsCollector;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import com.github.doodler.common.ws.handler.ChannelWsHandler;
import com.github.doodler.common.ws.handler.SessionContext;
import com.github.doodler.common.ws.handler.UserChannelWsHandler;
import com.github.doodler.common.ws.handler.UserSessionContext;
import com.github.doodler.common.ws.newsletter.SimpleWsMessageService;
import com.github.doodler.common.ws.security.WsUserSecurityCustomizer;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: WsServerConfig
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@EnableConfigurationProperties({WsServerProperties.class})
@Configuration(proxyBeanMethods = false)
public class WsServerConfig {

    @Bean
    public WsMessageFanoutAdviceContainer messageFanoutAdviceContainer() {
        return new WsMessageFanoutAdviceContainer();
    }

    @Bean
    public LifeCycleCallback lifeCycleCallback() {
        return new LifeCycleCallback();
    }

    @Bean
    public StdOutMessageWriter stdOutMessageWriter(
            WsMessageFanoutAdviceContainer messageFanoutAdviceContainer) {
        return new StdOutMessageWriter(messageFanoutAdviceContainer);
    }

    @Bean
    public OnlineNumberAccumulator onlineNumberAccumulator(
            RedisOperations<String, Object> redisOperations) {
        // RedisAtomicInteger websiteCounter =
        // RedisCounterUtils.getRedisIntegerCounter("online-number:website",
        // redisConnectionFactory, 0);
        // RedisAtomicInteger userCounter =
        // RedisCounterUtils.getRedisIntegerCounter("online-number:user",
        // redisConnectionFactory, 0);
        // RedisAtomicInteger chatCounter =
        // RedisCounterUtils.getRedisIntegerCounter("online-number:chat",
        // redisConnectionFactory, 0);
        return new OnlineNumberAccumulator(redisOperations);
    }

    @Bean
    public SessionContext sessionContext(WsStateChangeListenerContainer listenerContainer) {
        return new SessionContext(listenerContainer);
    }

    @Bean
    public UserSessionContext userSessionContext(WsStateChangeListenerContainer listenerContainer) {
        return new UserSessionContext(listenerContainer);
    }

    @Bean
    public WsStateChangeListenerContainer wsStateChangeListenerContainer() {
        return new WsStateChangeListenerContainer();
    }

    @Bean
    public SimpleWsMessageService simpleWsMessageService(SessionContext sessionContext,
            UserSessionContext userSessionContext, RedisPubSubService redisPubSubService,
            Marker marker) {
        return new SimpleWsMessageService(sessionContext, userSessionContext, redisPubSubService,
                marker);
    }

    @ConditionalOnMissingBean
    @Bean
    public WsDecoder wsDecoder(ObjectMapper objectMapper) {
        return new JacksonWsDecoder(objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public WsCodecFactory wsCodecFactory(WsDecoder wsDecoder) {
        return new DefaultWsCodecFactory(wsDecoder);
    }

    @Bean
    public ChannelWsHandler channelWsHandler(InstanceId instanceId, SessionContext sessionContext,
            WsStateChangeListenerContainer listenerContainer,
            WsMessageFanoutAdviceContainer adviceContainer, WsCodecFactory wsCodecFactory,
            RedisPubSubService redisPubSubService) {
        return new ChannelWsHandler(instanceId, sessionContext, listenerContainer, adviceContainer,
                wsCodecFactory, redisPubSubService);
    }

    @Bean
    public UserChannelWsHandler userChannelWsHandler(InstanceId instanceId,
            UserSessionContext sessionContext, WsStateChangeListenerContainer listenerContainer,
            WsMessageFanoutAdviceContainer adviceContainer, WsCodecFactory wsCodecFactory,
            RedisPubSubService redisPubSubService) {
        return new UserChannelWsHandler(instanceId, sessionContext, listenerContainer,
                adviceContainer, wsCodecFactory, redisPubSubService);
    }

    @ConditionalOnProperty("ws.server.keepalive.enabled")
    @Bean
    public KeepAliveBeater keepAliveBeater(SessionContext sessionContext,
            UserSessionContext userSessionContext, WsServerProperties serverConfig) {
        return new KeepAliveBeater(sessionContext, userSessionContext, serverConfig);
    }

    @Bean
    public MetricsCollector wsMetricsCollector(OnlineNumberAccumulator onlineNumberAccumulator,
            MeterRegistry registry) {
        return new WsMetricsCollector(onlineNumberAccumulator, registry);
    }

    @ConditionalOnApplication(value = {"crypto-newsletter-service", "crypto-chat-service"})
    @Bean
    public WsUserSecurityCustomizer wsUserSecurityCustomizer(WsServerProperties serverConfig,
            UserDetailsService userDetailsService) {
        return new WsUserSecurityCustomizer(serverConfig, userDetailsService);
    }
}
