package com.github.doodler.common.amqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: AmqpAdminProperties
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "spring.rabbitmq.provider")
public class AmqpAdminProperties {

    private String defaultQueue;

    private String defaultExchange;

    private String defaultExchangeType = "direct";

    private String defaultRoutingKey;

    private Binding[] bindings;

    @Getter
    @Setter
    @ToString
    public static class Queue {

        private String name;
        private boolean durable = true;
        private boolean autoDelete = false;
        private Map<String, Object> arguments;
    }

    @Getter
    @Setter
    @ToString
    public static class Exchange {

        private String name;
        private String type = "direct";
        private boolean durable = true;
        private boolean autoDelete = false;
        private Map<String, Object> arguments;
    }

    @Getter
    @Setter
    @ToString
    public static class Binding {

        private Queue queue;
        private Exchange exchange;
        private String routingKey;
        private Map<String, Object> arguments;
    }

    public List<Binding> getBindings() {
        List<Binding> list = new ArrayList<>();
        if (bindings != null) {
            list.addAll(Arrays.asList(bindings));
        }
        return list;
    }
}