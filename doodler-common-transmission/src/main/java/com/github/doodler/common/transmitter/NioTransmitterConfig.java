package com.github.doodler.common.transmitter;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.github.doodler.common.transmitter.utils.KryoSerializer;
import com.github.doodler.common.transmitter.utils.Serializer;

/**
 * 
 * @Description: NioTransmitterConfig
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@EnableConfigurationProperties({TransmitterNioProperties.class, TransmitterEventProperties.class})
@Configuration(proxyBeanMethods = false)
public class NioTransmitterConfig {

    @Autowired
    private TransmitterEventProperties eventProperties;

    @ConditionalOnMissingBean
    @Bean
    public Buffer<Packet> redisBuffer(RedisConnectionFactory redisConnectionFactory) {
        RedisBuffer buffer =
                new RedisBuffer(eventProperties.getRedis().getNamespace(), redisConnectionFactory);
        return buffer;
    }

    @Bean
    public NioServerBootstrap nioServerBootstrap(NioServer nioServer) {
        return new NioServerBootstrap(nioServer);
    }

    @Bean
    public NioClientBootstrap nioClientBootstrap(NioClient nioClient) {
        return new NioClientBootstrap(nioClient);
    }

    @ConditionalOnMissingBean
    @Bean
    public Serializer serializer() {
        return new KryoSerializer();
    }

    @ConditionalOnMissingBean
    @Bean
    public Partitioner partitioner() {
        return new MultipleChoicePartitioner();
    }

    @Bean
    public EventPublisher<Packet> eventPublisher(ThreadPoolTaskExecutor taskExecutor,
            Buffer<Packet> buffer) {
        return new EventPublisherImpl<>(taskExecutor, eventProperties.getMaxBufferCapacity(),
                eventProperties.getRequestFetchSize(), eventProperties.getTimeout(), buffer,
                eventProperties.getBufferCleanInterval());
    }

    @Autowired
    public void configure(EventPublisher<Packet> eventPublisher,
            List<EventSubscriber<Packet>> eventSubscribers) {
        if (CollectionUtils.isNotEmpty(eventSubscribers)) {
            eventPublisher.subscribe(eventSubscribers);
        }
    }

    @ConditionalOnProperty("doodler.transmitter.event.logging.enabled")
    @Bean
    public LoggingPacketSubscriber loggingPacketSubscriber() {
        return new LoggingPacketSubscriber();
    }

}
