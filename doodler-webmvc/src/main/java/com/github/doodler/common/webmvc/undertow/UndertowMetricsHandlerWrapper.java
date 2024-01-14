package com.github.doodler.common.webmvc.undertow;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.MetricsHandler;
import org.springframework.stereotype.Component;

/**
 * @Description: UndertowMetricsHandlerWrapper
 * @Author: Fred Feng
 * @Date: 21/11/2023
 * @Version 1.0.0
 */
@Component
public class UndertowMetricsHandlerWrapper implements HandlerWrapper {

    private MetricsHandler metricsHandler;

    @Override
    public HttpHandler wrap(HttpHandler handler) {
        metricsHandler = new MetricsHandler(handler);
        return metricsHandler;
    }

    public MetricsHandler getMetricsHandler() {
        return metricsHandler;
    }
}