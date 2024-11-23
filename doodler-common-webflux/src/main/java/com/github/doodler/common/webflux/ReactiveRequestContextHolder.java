package com.github.doodler.common.webflux;

import java.util.Optional;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * 
 * @Description: ReactiveRequestContextHolder
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@Component
public class ReactiveRequestContextHolder implements WebFilter {

    public static final String REQUEST_CONTEXT_KEY = ServerHttpRequest.class.getName();

    public static Mono<ServerHttpRequest> getCurrentRequest() {
        return Mono.deferContextual(context -> {
            Optional<ServerHttpRequest> request = context.getOrEmpty(REQUEST_CONTEXT_KEY);
            if (request.isPresent()) {
                ServerHttpRequest serverHttpRequest = request.get();
                return Mono.just(serverHttpRequest);
            } else {
                return null;
            }
        });
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange).contextWrite(Context.of(REQUEST_CONTEXT_KEY, request));
    }

}
