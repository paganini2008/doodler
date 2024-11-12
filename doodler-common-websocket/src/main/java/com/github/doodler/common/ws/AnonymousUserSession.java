package com.github.doodler.common.ws;

import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_GLOBAL;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import com.github.doodler.common.utils.MutableObservable;

/**
 * @Description: AnonymousUserSession
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
public class AnonymousUserSession implements WsSession {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WsUser user;
    protected final WebSocketSession session;
    protected final InstanceId instanceId;
    protected final WsCodecFactory wsCodecFactory;
    protected final RedisPubSubService pubSubService;

    public AnonymousUserSession(WebSocketSession session, String channel, InstanceId instanceId,
                                WsCodecFactory wsCodecFactory, RedisPubSubService pubSubService) {
        this(new AnonymousUser(channel, session.getId()), session, instanceId, wsCodecFactory,
                pubSubService);
    }

    public AnonymousUserSession(WsUser user, WebSocketSession session, InstanceId instanceId,
                                WsCodecFactory wsCodecFactory, RedisPubSubService pubSubService) {
        this.user = user;
        this.session = session;
        this.instanceId = instanceId;
        this.wsCodecFactory = wsCodecFactory;
        this.pubSubService = pubSubService;
    }

    private final MutableObservable hooks = new MutableObservable(false);
    private volatile long lastSentTimeMillis;

    @Override
    public WsUser getUser() {
        return user;
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public void sendSessionId() throws IOException {
        String text = wsCodecFactory.getDecoder().decode(user.getChannel(), user, user.getSessionId(), System.currentTimeMillis());
        sendText(text);
    }

    @Override
    public void sendObject(Object data, long timestamp) throws IOException {
        if (data != null) {
            String text = wsCodecFactory.getDecoder().decode(user.getChannel(), user, data, timestamp);
            sendText(text);
        }
    }

    @Override
    public void sendText(String text) throws IOException {
        if (!isOpen()) {
            return;
        }
        if (StringUtils.isNotBlank(text)) {
            session.sendMessage(new TextMessage(text));
            lastSentTimeMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void sendPing() throws IOException {
        if (!isOpen()) {
            return;
        }
        session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
    }

    @Override
    public void sendPong() throws IOException {
        if (!isOpen()) {
            return;
        }
        session.sendMessage(new PongMessage(ByteBuffer.wrap("pong".getBytes())));
    }

    @Override
    public long getLastSentTimeMillis() {
        return lastSentTimeMillis;
    }

    @Override
    public void fanout(Object object, String... includedSessionIds) {
        WsMessageEntity messageEntity = new WsMessageEntity(instanceId.get(), user, object, includedSessionIds);
        pubSubService.convertAndMulticast(PUBSUB_CHANNEL_FANOUT_GLOBAL, messageEntity);
    }

    @Override
    public void disconnect(String reason) throws IOException {
        if (isOpen()) {
            session.close(new CloseStatus(CloseStatus.NORMAL.getCode(), reason));
        }
    }

    @Override
    public void addDisposableHandler(DisposableHandler disposableHandler) {
        if (disposableHandler != null) {
            hooks.addObserver("disposable", (ob, arg) -> {
                disposableHandler.dispose((WsSession) arg);
            });
        }
    }

    @Override
    public void destroy(String reason) throws IOException {
        disconnect(reason);
        hooks.notifyObservers("disposable", this);
    }

    @Override
    public String toString() {
        return String.format("%s:  - %s", getUser(), session);
    }
}