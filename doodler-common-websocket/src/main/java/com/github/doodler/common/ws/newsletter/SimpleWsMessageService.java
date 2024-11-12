package com.github.doodler.common.ws.newsletter;

import static com.github.doodler.common.ws.WsContants.CHANNEL_USER;
import static com.github.doodler.common.ws.WsContants.CHANNEL_WEBSITE;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Marker;
import com.github.doodler.common.redis.pubsub.RedisPubSub;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import com.github.doodler.common.ws.WsMessageService;
import com.github.doodler.common.ws.WsSession;
import com.github.doodler.common.ws.handler.SessionContext;
import com.github.doodler.common.ws.handler.UserSessionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: SimpleWsMessageService
 * @Author: Fred Feng
 * @Date: 23/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleWsMessageService implements WsMessageService {

    private static final String PUBSUB_CHANNEL_FANOUT_SEND_OBJECT = "FANOUT-SEND-OBJECT";
    private static final String PUBSUB_CHANNEL_FANOUT_SEND_USER_OBJECT = "FANOUT-SEND-USER-OBJECT";

    private final SessionContext sessionContext;
    private final UserSessionContext userSessionContext;
    private final RedisPubSubService redisPubSubService;
    private final Marker marker;

    @Override
    public void sendObject(Object payload) {
        Notification notification = new Notification();
        notification.setPayload(payload);
        redisPubSubService.convertAndMulticast(PUBSUB_CHANNEL_FANOUT_SEND_OBJECT, notification);
    }

    @Override
    public void sendObject(Long userId, Object payload) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setPayload(payload);
        redisPubSubService.convertAndMulticast(PUBSUB_CHANNEL_FANOUT_SEND_USER_OBJECT, notification);
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_SEND_OBJECT)
    public synchronized void onSendObject(String channel, Object data) {
        Notification notification = (Notification) data;
        List<WsSession> sessions = sessionContext.getSessions(CHANNEL_WEBSITE);
        if (log.isInfoEnabled()) {
            log.info(marker, "Boardcast msg: {}, sessions: {}", data, sessions != null ? sessions.size() : 0);
        }
        if (CollectionUtils.isNotEmpty(sessions)) {
            sessions.get(0).fanout(notification.getPayload());
        }
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_SEND_USER_OBJECT)
    public synchronized void onSendObjectToUser(String channel, Object data) {
        Notification notification = (Notification) data;
        List<WsSession> wsSessions = userSessionContext.getSessions(CHANNEL_USER, notification.getUserId());
        if (log.isInfoEnabled()) {
            log.info(marker, "Boardcast msg: {}, sessions: {}", data, wsSessions != null ? wsSessions.size() : 0);
        }
        if (CollectionUtils.isNotEmpty(wsSessions)) {
            wsSessions.get(0).fanout(notification.getPayload());
        }
    }
}