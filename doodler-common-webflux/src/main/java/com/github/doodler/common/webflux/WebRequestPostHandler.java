package com.github.doodler.common.webflux;

import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;

import com.github.doodler.common.ApiResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * WebRequestPostHandler
 *
 * @author Fred Feng
 * @version 1.0.0
 */
@SuppressWarnings("all")
public class WebRequestPostHandler extends ResponseBodyResultHandler {

    private static MethodParameter delegateMethodParameter;

    static {
        try {
            delegateMethodParameter = new MethodParameter(WebRequestPostHandler.class.getDeclaredMethod(
                    "methodForReturnValue"), -1);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public WebRequestPostHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        super(writers, resolver);
    }

    public WebRequestPostHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver,
                                 ReactiveAdapterRegistry registry) {
        super(writers, resolver, registry);
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        Object body;
        if (returnValue instanceof Mono) {
            body = ((Mono<Object>) result.getReturnValue()).map(data -> wrapResponseBody(exchange, data)).defaultIfEmpty(
                    ApiResult.ok());
        } else if (returnValue instanceof Flux) {
            body = ((Flux<Object>) result.getReturnValue()).collectList().map(data -> wrapResponseBody(exchange,
                    data)).defaultIfEmpty(ApiResult.ok());
        } else {
            body = Mono.just(wrapResponseBody(exchange, returnValue));
        }
        return writeBody(body, delegateMethodParameter, exchange);
    }

    private static Mono<ApiResult<?>> methodForReturnValue() {
        return null;
    }

    private static ApiResult<?> wrapResponseBody(ServerWebExchange exchange, Object result) {
        ApiResult apiResult;
        if (result instanceof ApiResult) {
            apiResult = (ApiResult<?>) result;
        } else {
            apiResult = ApiResult.ok(result);
        }
        String timestamp = exchange.getRequest().getHeaders().getFirst(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            apiResult.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        apiResult.setRequestPath(exchange.getRequest().getPath().pathWithinApplication().value());
        return apiResult;
    }

}
