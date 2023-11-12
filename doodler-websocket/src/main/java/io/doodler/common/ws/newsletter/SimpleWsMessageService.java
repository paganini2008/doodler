package io.doodler.common.ws.newsletter;

import static io.doodler.common.ws.WsContants.CHANNEL_USER;
import static io.doodler.common.ws.WsContants.CHANNEL_WEBSITE;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import io.doodler.common.redis.pubsub.RedisPubSub;
import io.doodler.common.redis.pubsub.RedisPubSubService;
import io.doodler.common.ws.WsMessageService;
import io.doodler.common.ws.WsSession;
import io.doodler.common.ws.handler.SessionContext;
import io.doodler.common.ws.handler.UserSessionContext;
import lombok.RequiredArgsConstructor;

/**
 * @Description: SimpleWsMessageService
 * @Author: Fred Feng
 * @Date: 23/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SimpleWsMessageService implements WsMessageService {

    private static final String PUBSUB_CHANNEL_FANOUT_SEND_OBJECT = "FANOUT-SEND-OBJECT";
    private static final String PUBSUB_CHANNEL_FANOUT_SEND_USER_OBJECT = "FANOUT-SEND-USER-OBJECT";

    private final SessionContext sessionContext;
    private final UserSessionContext userSessionContext;
    private final RedisPubSubService redisPubSubService;

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
        if (CollectionUtils.isNotEmpty(sessions)) {
            sessions.get(0).fanout(notification.getPayload());
        }
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_SEND_USER_OBJECT)
    public synchronized void onSendObjectToUser(String channel, Object data) {
        Notification notification = (Notification) data;
        List<WsSession> wsSessions = userSessionContext.getSessions(CHANNEL_USER, notification.getUserId());
        if (CollectionUtils.isNotEmpty(wsSessions)) {
            wsSessions.get(0).fanout(notification.getPayload());
        }
    }
}