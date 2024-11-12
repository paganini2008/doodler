package com.github.doodler.common.ws;

import static com.github.doodler.common.ws.WsContants.CHANNEL_CHAT;
import static com.github.doodler.common.ws.WsContants.CHANNEL_USER;
import static com.github.doodler.common.ws.WsContants.CHANNEL_WEBSITE;
import static com.github.doodler.common.ws.WsContants.KEY_ONLINE_CHAT_USERS;
import static com.github.doodler.common.ws.WsContants.KEY_ONLINE_SESSIONS;
import static com.github.doodler.common.ws.WsContants.KEY_ONLINE_USERS;
import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;

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

    private final RedisOperations<String, Object> redisOperations;

    public int onlineNumberOfWebsite() {
        Long result = redisOperations.opsForList().size(KEY_ONLINE_SESSIONS);
        return result != null ? result.intValue() : 0;
    }

    public int onlineNumberOfUsers() {
        Long result = redisOperations.opsForList().size(KEY_ONLINE_USERS);
        return result != null ? result.intValue() : 0;
    }

    public int onlineNumberOfChat() {
        Long result = redisOperations.opsForList().size(KEY_ONLINE_CHAT_USERS);
        return result != null ? result.intValue() : 0;
    }

    @Override
    public void onOpen(WsSession session) throws IOException {
        String channel = session.getUser().getChannel();
        switch (channel) {
            case CHANNEL_WEBSITE:
                redisOperations.opsForList().leftPush(KEY_ONLINE_SESSIONS, session.getUser().getSessionId());
                break;
            case CHANNEL_USER:
                redisOperations.opsForList().leftPush(KEY_ONLINE_USERS, session.getUser().getSessionId());
                break;
            case CHANNEL_CHAT:
                redisOperations.opsForList().leftPush(KEY_ONLINE_CHAT_USERS, session.getUser().getSessionId());
                break;
            default:
                throw new UnsupportedOperationException("Unknown channel: " + channel);
        }
    }

    @Override
    public void onClose(WsSession session, int code, String reason) throws IOException {
        if (session == null) {
            return;
        }
        String channel = session.getUser().getChannel();
        switch (channel) {
            case CHANNEL_WEBSITE:
                redisOperations.opsForList().remove(KEY_ONLINE_SESSIONS, 1, session.getUser().getSessionId());
                break;
            case CHANNEL_USER:
                redisOperations.opsForList().remove(KEY_ONLINE_USERS, 1, session.getUser().getSessionId());
                break;
            case CHANNEL_CHAT:
                redisOperations.opsForList().remove(KEY_ONLINE_CHAT_USERS, 1, session.getUser().getSessionId());
                break;
            default:
                throw new UnsupportedOperationException("Unknown channel: " + channel);
        }
    }
}