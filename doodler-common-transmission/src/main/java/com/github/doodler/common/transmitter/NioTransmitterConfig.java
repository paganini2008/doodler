package com.github.doodler.common.transmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import com.github.doodler.common.transmitter.utils.KryoSerializer;
import com.github.doodler.common.transmitter.utils.Serializer;

/**
 * 
 * @Description: NioTransmitterConfig
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@EnableConfigurationProperties({TransmitterNioProperties.class, TransmitterBufferProperties.class})
@Configuration(proxyBeanMethods = false)
public class NioTransmitterConfig {

    @Autowired
    private TransmitterBufferProperties bufferProperties;

    @ConditionalOnMissingBean
    @Bean
    public BufferArea redisBufferZone(RedisConnectionFactory redisConnectionFactory) {
        RedisBufferArea bufferArea = new RedisBufferArea(bufferProperties, redisConnectionFactory);
        return bufferArea;
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

}
