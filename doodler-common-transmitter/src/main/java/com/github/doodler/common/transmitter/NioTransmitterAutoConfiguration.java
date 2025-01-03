package com.github.doodler.common.transmitter;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.events.Buffer;
import com.github.doodler.common.events.EventPublisher;
import com.github.doodler.common.events.EventPublisherImpl;
import com.github.doodler.common.events.EventSubscriber;
import com.github.doodler.common.transmitter.netty.NettyTransportAutoConfiguration;
import com.github.doodler.common.transmitter.serializer.KryoSerializer;
import com.github.doodler.common.transmitter.serializer.Serializer;

/**
 * 
 * @Description: NioTransmitterAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@EnableConfigurationProperties({TransmitterNioProperties.class, TransmitterEventProperties.class})
@Import({NettyTransportAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
public class NioTransmitterAutoConfiguration {

    @Autowired
    private TransmitterNioProperties nioProperties;

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

    @DependsOn("nioServerBootstrap")
    @Bean
    public NioClientBootstrap nioClientBootstrap(NioClient nioClient,
            ApplicationInfoManager applicationInfoManager) {
        return new NioClientBootstrap(nioProperties, nioClient, applicationInfoManager);
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
            Buffer<Packet> buffer, List<EventSubscriber<Packet>> eventSubscribers) {
        EventPublisher<Packet> eventPublisher = new EventPublisherImpl<>(taskExecutor,
                eventProperties.getMaxBufferCapacity(), eventProperties.getRequestFetchSize(),
                eventProperties.getTimeout(), buffer, eventProperties.getBufferCleanInterval());
        if (CollectionUtils.isNotEmpty(eventSubscribers)) {
            eventPublisher.subscribe(eventSubscribers);
        }
        return eventPublisher;
    }



    @ConditionalOnProperty("doodler.transmitter.event.logging.enabled")
    @Bean
    public LoggingPacketSubscriber loggingPacketSubscriber() {
        return new LoggingPacketSubscriber();
    }

    @Bean
    public PacketHandlerExecution packetHandlerExecution(List<PacketHandler> packetHandlers) {
        return new PacketHandlerExecution(packetHandlers);
    }

}
