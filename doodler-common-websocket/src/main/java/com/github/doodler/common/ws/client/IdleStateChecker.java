package com.github.doodler.common.ws.client;

import java.util.Timer;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: IdleStateChecker
 * @Author: Fred Feng
 * @Date: 22/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class IdleStateChecker extends TimerTask {

    private final WsConnection connection;
    private final long maxIdleTime;
    private final Timer timer;
    private volatile long lastSentTime;

    public IdleStateChecker(WsConnection connection, long maxIdleTime) {
        if (maxIdleTime <= 1000) {
            throw new IllegalArgumentException("MaxIdleTime is too short");
        }
        this.connection = connection;
        this.maxIdleTime = maxIdleTime;
        this.timer = new Timer();
        this.timer.schedule(this, 5000L);
    }

    public void setLastSentTime(long lastSentTime) {
        this.lastSentTime = lastSentTime;
    }

    @Override
    public void run() {
        if (lastSentTime == 0 || connection.isClosed()) {
            return;
        }
        long idleTime = System.currentTimeMillis() - lastSentTime;
        if (maxIdleTime > 0 && idleTime > maxIdleTime) {
            if (log.isWarnEnabled()) {
                log.warn("Release idle websocket connection: {}", connection);
            }
            connection.close(false);
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}