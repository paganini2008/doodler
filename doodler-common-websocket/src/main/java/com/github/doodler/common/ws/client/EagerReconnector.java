package com.github.doodler.common.ws.client;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EagerReconnector
 * @Author: Fred Feng
 * @Date: 22/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class EagerReconnector implements Reconnector {

    private final int maxRetries;
    private final long interval;
    private final AtomicInteger retries = new AtomicInteger();

    public EagerReconnector(long interval) {
        this(-1, interval);
    }

    public EagerReconnector(int maxRetries, long interval) {
        this.maxRetries = maxRetries;
        this.interval = Math.max(interval, 5000L);
    }

    @Override
    public void reset() {
    	retries.set(0);
    }

    @Override
    public boolean shouldReconnect(WsConnection connection) {
        return (!connection.isAbandoned() && connection.isClosed()) && ((maxRetries < 0) || (retries.get() < maxRetries));
    }

    @Override
    public void onClosing(WsConnection connection) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
        }
        retries.incrementAndGet();
        if (log.isInfoEnabled()) {
            log.info("Retry {} times for recreating new Websocket connection.", retries.get());
        }
        connection.retrieve();
    }
}