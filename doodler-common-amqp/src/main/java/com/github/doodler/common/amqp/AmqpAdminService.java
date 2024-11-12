package com.github.doodler.common.amqp;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @Description: AmqpAdminService
 * @Author: Fred Feng
 * @Date: 18/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class AmqpAdminService implements InitializingBean {

    private final AmqpAdmin amqpAdmin;
    private final AmqpAdminProperties adminConfig;
    private final Map<String, AmqpAdminProperties.Binding> routingKeyAndBinding = new ConcurrentHashMap<>();
    private final PathMatcher pathMatcher = new AntPathMatcher(".");

    public AmqpAdminProperties.Binding getBindingByRoutingKey(String routingKey) {
        AmqpAdminProperties.Binding binding = routingKeyAndBinding.get(routingKey);
        if (binding == null) {
            binding = doGetBindingByRoutingKey(routingKey);
            if (binding != null) {
                routingKeyAndBinding.putIfAbsent(routingKey, binding);
                binding = routingKeyAndBinding.get(routingKey);
            }
        }
        return binding;
    }

    public String getExchangeByRoutingKey(String routingKey) {
        AmqpAdminProperties.Binding binding = getBindingByRoutingKey(routingKey);
        if (binding != null) {
            return binding.getExchange().getName();
        }
        return null;
    }

    public String getQueueByRoutingKey(String routingKey) {
        AmqpAdminProperties.Binding binding = getBindingByRoutingKey(routingKey);
        if (binding != null) {
            return binding.getQueue().getName();
        }
        return null;
    }

    private AmqpAdminProperties.Binding doGetBindingByRoutingKey(String routingKey) {
        Optional<AmqpAdminProperties.Binding> opt = adminConfig.getBindings().stream()
                .filter(info -> matchesRoutingKey(info.getRoutingKey(), routingKey)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    private boolean matchesRoutingKey(String pattern, String routingKey) {
        if (pattern.equals(routingKey)) {
            return true;
        }
        if (pattern.indexOf('#') != -1) {
            pattern = pattern.replace("#", "**");
        }
        return pathMatcher.match(pattern, routingKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(adminConfig.getDefaultQueue())) {
            amqpAdmin.declareQueue(new Queue(adminConfig.getDefaultQueue(), true));
        }
        if (StringUtils.isNotBlank(adminConfig.getDefaultExchange())) {
            amqpAdmin.declareExchange(createExchange(adminConfig.getDefaultExchange(), "direct", true, false, null));
        }
        if (StringUtils.isNotBlank(adminConfig.getDefaultQueue()) &&
                StringUtils.isNotBlank(adminConfig.getDefaultExchange()) &&
                StringUtils.isNotBlank(adminConfig.getDefaultRoutingKey())) {
            amqpAdmin.declareBinding(new Binding(adminConfig.getDefaultQueue(), Binding.DestinationType.QUEUE,
                    adminConfig.getDefaultExchange(),
                    adminConfig.getDefaultRoutingKey(), null));
        }

        List<AmqpAdminProperties.Binding> bindings = adminConfig.getBindings();
        if (CollectionUtils.isNotEmpty(bindings)) {
            for (AmqpAdminProperties.Binding bindingConfig : bindings) {
                AmqpAdminProperties.Queue queueConfig = bindingConfig.getQueue();
                AmqpAdminProperties.Exchange exchangeConfig = bindingConfig.getExchange();
                amqpAdmin.declareQueue(
                        new Queue(queueConfig.getName(), queueConfig.isDurable(), false, queueConfig.isAutoDelete(),
                                queueConfig.getArguments()));
                amqpAdmin.declareExchange(
                        createExchange(exchangeConfig.getName(), exchangeConfig.getType(), exchangeConfig.isDurable(),
                                exchangeConfig.isAutoDelete(), exchangeConfig.getArguments()));
                amqpAdmin.declareBinding(
                        new Binding(queueConfig.getName(), Binding.DestinationType.QUEUE, exchangeConfig.getName(),
                                bindingConfig.getRoutingKey(), bindingConfig.getArguments()));
            }
        }
    }

    private Exchange createExchange(String exchangeName, String exchangeType, boolean durable, boolean autoDelete,
                                    Map<String, Object> arguments) {
        switch (exchangeType) {
            case "direct":
                return new DirectExchange(exchangeName, durable, autoDelete, arguments);
            case "fanout":
                return new FanoutExchange(exchangeName, durable, autoDelete, arguments);
            case "headers":
                return new HeadersExchange(exchangeName, durable, autoDelete, arguments);
            case "topic":
                return new TopicExchange(exchangeName, durable, autoDelete, arguments);
            default:
                throw new AmqpIllegalStateException("Unsupported exchange type: " + exchangeType);
        }
    }
}