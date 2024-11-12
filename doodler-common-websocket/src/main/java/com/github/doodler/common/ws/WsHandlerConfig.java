package com.github.doodler.common.ws;

import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.ws.handler.ChannelWsHandler;
import com.github.doodler.common.ws.handler.UserChannelWsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Description: WsHandlerConfig
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
@AutoConfigureAfter({WsServerConfig.class})
@Configuration(proxyBeanMethods = false)
@EnableWebSocket
public class WsHandlerConfig implements WebSocketConfigurer {
	
	@Autowired
	private InstanceId instanceId;

    @Autowired
    private WsServerProperties serverConfig;

    @Autowired
    private RedisOperations<String, Object> redisOperations;

    @Autowired
    private ChannelWsHandler channelWsHandler;

    @Autowired
    private UserChannelWsHandler userChannelWsHandler;

    @Bean
    public SecurityHandshakeInterceptor securityHandshakeInterceptor() {
        return new SecurityHandshakeInterceptor(serverConfig, redisOperations, instanceId);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(channelWsHandler, "/ws/website/**").addInterceptors(
                securityHandshakeInterceptor()).setAllowedOrigins("*");
        registry.addHandler(channelWsHandler, "/sockjs/ws/website/**").addInterceptors(
                securityHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
        registry.addHandler(userChannelWsHandler, "/ws/user/**").addInterceptors(
                securityHandshakeInterceptor()).setAllowedOrigins("*");
        registry.addHandler(userChannelWsHandler, "/sockjs/ws/user/**").addInterceptors(
                securityHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }
}