package io.doodler.ws;

import static io.doodler.ws.WsContants.CHANNEL_CHAT;
import static io.doodler.ws.WsContants.CHANNEL_USER;
import static io.doodler.ws.WsContants.CHANNEL_WEBSITE;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: OnlineNumberAccumulator
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Slf4j
@Order(80)
@RequiredArgsConstructor
public class OnlineNumberAccumulator implements WsStateChangeListener {

    private final RedisAtomicInteger websiteCounter;
    private final RedisAtomicInteger userCounter;
    private final RedisAtomicInteger chatCounter;

    public int onlineNumberOfWebsite() {
        return websiteCounter.get();
    }

    public int onlineNumberOfUsers() {
        return userCounter.get();
    }

    public int onlineNumberOfChat() {
        return chatCounter.get();
    }

    @Override
    public void onOpen(WsSession session) throws IOException {
        long count = 0;
        String channel = session.getUser().getChannel();
        switch (channel) {
            case CHANNEL_WEBSITE:
                count = websiteCounter.incrementAndGet();
                break;
            case CHANNEL_USER:
                count = userCounter.incrementAndGet();
                break;
            case CHANNEL_CHAT:
                count = chatCounter.incrementAndGet();
                break;
            default:
                throw new UnsupportedOperationException("Unknown channel: " + channel);
        }
        log(channel, count);
    }

    @Override
    public void onClose(WsSession session, int code, String reason) throws IOException {
        if (session == null) {
            return;
        }
        long count = 0;
        String channel = session.getUser().getChannel();
        switch (channel) {
            case CHANNEL_WEBSITE:
                count = websiteCounter.decrementAndGet();
                break;
            case CHANNEL_USER:
                count = userCounter.decrementAndGet();
                break;
            case CHANNEL_CHAT:
                count = chatCounter.decrementAndGet();
                break;
            default:
                throw new UnsupportedOperationException("Unknown channel: " + channel);
        }
        log(channel, count);
    }

    private void log(String channel, long count) {
        if (log.isInfoEnabled()) {
            log.info("[{}] online user number: {}", channel, count);
        }
    }

}