package com.github.doodler.common.transmitter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 
 * @Description: TransmitterBufferProperties
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties("doodler.transmitter.buffer")
public class TransmitterBufferProperties {

    private String defaultCollectionName = "default";
    private int batchSize = 10;

    private InMemoryBufferArea memory;
    private RedisBufferArea redis;

    @Data
    public static class InMemoryBufferArea {

        private int maxSize;

    }

    @Data
    public static class RedisBufferArea {

        private String namespace = "default";

    }

}
