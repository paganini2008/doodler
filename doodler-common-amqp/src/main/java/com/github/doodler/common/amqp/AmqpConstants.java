package com.github.doodler.common.amqp;

/**
 * @Description: AmqpConstants
 * @Author: Fred Feng
 * @Date: 19/01/2023
 * @Version 1.0.0
 */
public interface AmqpConstants {

    String DEFAULT_DIRECT_EXCHANGE_NAME = "crypto.exchange.direct";

    String DEFAULT_FANOUT_EXCHANGE_NAME = "crypto.exchange.fanout";

    String AMQP_HEADER_TYPE_NAME = "__TypeId__";
    
    String AMQP_HEADER_EVENT_TYPE = "__eventType__";
    
    String AMQP_HEADER_EVENT_SENDER = "__eventSender__";

    String AMQP_HEADER_CONSUMER_MAX_RETRIES = "__consumerMaxRetries__";
    
    int DEFAULT_MAX_CONSUMER_RETRIES = 10;
    
    String DEFAULT_DEADLETTER_EXCHANGE_NAME = "crypto.exchange.deadletter";
    
    String DEFAULT_DEADLETTER_QUEUE_NAME = "crypto.queue.deadletter";
    
    String DEFAULT_DEADLETTER_ROUTING_KEY_NAME = "crypto.routing-key.deadletter";
    
    String DEFAULT_DEADLETTER_MESSAGE_EXPIRATION = "60000";
}