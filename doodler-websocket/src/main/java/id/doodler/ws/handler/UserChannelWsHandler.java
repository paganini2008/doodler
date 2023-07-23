package id.doodler.ws.handler;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import id.doodler.ws.BasicUserSession;
import id.doodler.ws.WsCodecFactory;
import id.doodler.ws.WsMessageFanoutAdviceContainer;
import id.doodler.ws.WsSession;
import id.doodler.ws.WsStateChangeListenerContainer;
import id.doodler.ws.WsUser;
import id.doodler.ws.WsUserDetails;
import io.doodler.common.context.InstanceId;
import io.doodler.redis.pubsub.RedisPubSubService;
import io.doodler.security.IdentifiableUserDetails;
import io.doodler.security.InternalAuthenticationToken;
import io.doodler.security.RegularUser;
import io.doodler.security.Visitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: UserChannelWsHandler
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class UserChannelWsHandler implements WebSocketHandler{

    private final InstanceId instanceId;
    private final UserSessionContext sessionContext;
    private final WsStateChangeListenerContainer stateChangeListenerContainer;
    private final WsMessageFanoutAdviceContainer messageFanoutAdviceContainer;
    private final WsCodecFactory wsCodecFactory;
    private final RedisPubSubService redisPubSubService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String channel = (String) session.getAttributes().get("channel");
        String identifier = (String) session.getAttributes().get("identifier");
        UserDetails userDetails = retrieveUserDetails(session, identifier);
        BasicUserSession wsSession = new BasicUserSession(session, channel, (IdentifiableUserDetails) userDetails,
                instanceId, wsCodecFactory, redisPubSubService);
        sessionContext.addSession(channel, wsSession.getUser(), wsSession);
        session.getAttributes().put("user", wsSession.getUser());
        if (log.isInfoEnabled()) {
            log.info("Open new session: {}, onlineSize: {}", wsSession, sessionContext.countOfSessions(channel));
        }
        stateChangeListenerContainer.triggerIfOpen(wsSession);
    }

    private UserDetails retrieveUserDetails(WebSocketSession session, String identifier) {
        Object principal = session.getPrincipal();
        if (principal instanceof InternalAuthenticationToken) {
            Object result = ((InternalAuthenticationToken) principal).getPrincipal();
            if (result instanceof WsUserDetails) {
                return (WsUserDetails) result;
            } else if (result instanceof RegularUser) {
                return new WsUserDetails((RegularUser) result);
            }
        }
        return new Visitor(Collections.singletonMap("sessionId", session.getId()));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message == null) {
            return;
        }
        if (message instanceof PingMessage) {
            session.sendMessage(new TextMessage("pong"));
        } else {
            Object payload = message.getPayload();
            if (!(payload instanceof String)) {
                return;
            }
            String text = payload.toString();
            if (log.isDebugEnabled()) {
                log.debug(text);
            }
            if (("{ping}".equalsIgnoreCase((String) payload))) {
                session.sendMessage(new TextMessage("{pong}"));
            } else {
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
                    closeStatus.getReason(),
                    sessionContext.countOfSessions(channel));
        }
        stateChangeListenerContainer.triggerIfClose(wsSession, closeStatus.getCode(), closeStatus.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}