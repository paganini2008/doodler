package io.doodler.redis.pubsub;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import io.doodler.redis.RedisConfig;
import io.doodler.redis.RedisSerializerUtils;

/**
 * @Description: RedisPubSubConfig
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
@AutoConfigureAfter({RedisConfig.class})
@Configuration(proxyBeanMethods = false)
public class RedisPubSubConfig {

    @Value("${spring.application.redis.pubsub.channel:}")
    private String pubsubChannel;

    @Value("${spring.application.redis.pubsub.keyNamespace:pubsub}")
    private String keyNamespace;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public RedisMessageEventPublisher redisMessageEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        return new RedisMessageEventPublisher(keyNamespace, redisTemplate);
    }

    @Bean
    public RedisMessageEventDispatcher redisMessageEventDispatcher() {
        return new RedisMessageEventDispatcher();
    }

    @Bean
    public RedisPubSubService redisPubSubService(RedisConnectionFactory redisConnectionFactory) {
        return new RedisPubSubServiceImpl(redisConnectionFactory, keyNamespace, getChannel());
    }

    @Bean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
            RedisMessageListenerContainer redisMessageListenerContainer) {
        KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
        listener.setKeyspaceNotificationsConfigParameter("Ex");
        return listener;
    }

    @Bean
    public MessageListenerAdapter redisMessageEventListener(RedisMessageEventPublisher eventPublisher) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(eventPublisher, "doPubSub");
        adapter.setSerializer(RedisSerializerUtils.getJacksonRedisSerializer());
        adapter.afterPropertiesSet();
        return adapter;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageEventListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                            MessageListenerAdapter messageListener) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(getChannel()));
        return redisMessageListenerContainer;
    }

    private String getChannel() {
        return StringUtils.isNotBlank(pubsubChannel) ? pubsubChannel : applicationName;
    }
}