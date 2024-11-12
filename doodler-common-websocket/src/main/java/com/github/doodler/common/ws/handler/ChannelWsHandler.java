package com.github.doodler.common.ws.handler;

import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import com.github.doodler.common.ws.AnonymousUserSession;
import com.github.doodler.common.ws.WsCodecFactory;
import com.github.doodler.common.ws.WsMessageFanoutAdviceContainer;
import com.github.doodler.common.ws.WsSession;
import com.github.doodler.common.ws.WsStateChangeListenerContainer;
import com.github.doodler.common.ws.WsUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description: ChannelWsHandler
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class ChannelWsHandler implements WebSocketHandler {

    private final InstanceId instanceId;
    private final SessionContext sessionContext;
    private final WsStateChangeListenerContainer stateChangeListenerContainer;
    private final WsMessageFanoutAdviceContainer messageFanoutAdviceContainer;
    private final WsCodecFactory wsCodecFactory;
    private final RedisPubSubService redisPubSubService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String channel = (String) session.getAttributes().get("channel");
        AnonymousUserSession wsSession = new AnonymousUserSession(session, channel, instanceId,
                wsCodecFactory,
                redisPubSubService);
        sessionContext.addSession(channel, wsSession.getUser(), wsSession);
        session.getAttributes().put("user", wsSession.getUser());
        if (log.isInfoEnabled()) {
            log.info("Open new session: {}, onlineSize: {}", wsSession, sessionContext.countOfSessions(channel));
        }
        stateChangeListenerContainer.triggerIfOpen(wsSession);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message == null) {
            return;
        }
        if (message instanceof PongMessage) {
            session.sendMessage(new TextMessage("ping"));
        } else {
            Object payload = message.getPayload();
            if (!(payload instanceof String)) {
                return;
            }
            String text = payload.toString();
            if (log.isDebugEnabled()) {
                log.debug(text);
            }

            String channel = (String) session.getAttributes().get("channel");
            WsUser user = (WsUser) session.getAttributes().get("user");
            WsSession wsSession = sessionContext.getSession(channel, user);
            if (wsSession != null) {
                Object object = wsCodecFactory.getEncoder().encode(channel, user, text);
                if (object != null) {
                    object = messageFanoutAdviceContainer.triggerPreFanout(user, object);
                    if (object != null) {
                        wsSession.fanout(object);
                    }
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (closeStatus == null) {
            closeStatus = new CloseStatus(CloseStatus.NORMAL.getCode(), "<None>");
        }
        String channel = (String) session.getAttributes().get("channel");
        WsUser user = (WsUser) session.getAttributes().get("user");
        WsSession wsSession = sessionContext.removeSession(channel, user);
        if (log.isInfoEnabled()) {
            log.info("Close session: {}, code: {}, reason: {}, onlineSize: {}", wsSession, closeStatus.getCode(),
                    closeStatus.getReason(), sessionContext.countOfSessions(channel));
        }
        stateChangeListenerContainer.triggerIfClose(wsSession, closeStatus.getCode(), closeStatus.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}