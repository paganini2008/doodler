package com.github.doodler.common.webflux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @Description: CachedRequestBodylFilter
 * @Author: Fred Feng
 * @Date: 18/11/2024
 * @Version 1.0.0
 */
@Slf4j
@Component
public class CachedRequestBodylFilter implements WebFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String contentType = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        log.info("handle request body data, filter:[{}], contentType:[{}]", this.getClass().getName(), contentType);
        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            AtomicReference<String> bodyRef = new AtomicReference<>();
            return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {

                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
                DataBufferUtils.retain(dataBuffer);
                bodyRef.set(charBuffer.toString());
                String bodyStr = bodyRef.get();
                log.info("read request body:\n" + bodyStr);
                Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0,
                        dataBuffer.readableByteCount())));
                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                        exchange.getRequest()) {

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
                };
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            });

        }
        return chain.filter(exchange);
    }
}
