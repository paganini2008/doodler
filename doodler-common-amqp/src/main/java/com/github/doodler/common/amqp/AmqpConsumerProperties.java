package com.github.doodler.common.amqp;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: AmqpConsumerProperties
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.rabbitmq.consumer")
public class AmqpConsumerProperties {

    private List<String> queues;
    private int ack;
}