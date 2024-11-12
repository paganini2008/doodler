package com.github.doodler.common.ws.handler;

import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_GLOBAL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.github.doodler.common.redis.pubsub.RedisPubSub;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.ws.WsMessageEntity;
import com.github.doodler.common.ws.WsSession;
import com.github.doodler.common.ws.WsStateChangeListenerContainer;
import com.github.doodler.common.ws.WsUser;
import lombok.RequiredArgsConstructor;

/**
 * @Description: SessionContext
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SessionContext {

    private final Map<String, Map<WsUser, WsSession>> channelSessions = new ConcurrentHashMap<>();
    private final WsStateChangeListenerContainer stateChangeListenerContainer;

    public void addSession(String channel, WsUser user, WsSession wsSession) {
        Map<WsUser, WsSession> map = MapUtils.getOrCreate(channelSessions, channel, ConcurrentHashMap::new);
        if (user != null) {
            map.put(user, wsSession);
        }
    }

    public WsSession removeSession(String channel, WsUser user) {
        WsSession wsSession = null;
        Map<WsUser, WsSession> map = channelSessions.get(channel);
        if (map != null && user != null) {
            wsSession = map.remove(user);
        }
        if (MapUtils.isEmpty(map)) {
            channelSessions.remove(channel);
        }
        return wsSession;
    }

    public WsSession getSession(String channel, WsUser user) {
        Map<WsUser, WsSession> map = channelSessions.get(channel);
        return map != null && user != null ? map.get(user) : null;
    }
    
    public List<WsSession> getAllWsSessions() {
        List<WsSession> sessions = new ArrayList<>();
        channelSessions.values().forEach(m -> {
            sessions.addAll(m.values());
        });
        return sessions;
    }

    public List<WsSession> getSessions(String channel) {
        if (!channelSessions.containsKey(channel)) {
            return Collections.emptyList();
        }
        return new ArrayList<WsSession>(channelSessions.get(channel).values());
    }

    public int countOfSessions(String channel) {
        return channelSessions.containsKey(channel) ? channelSessions.get(channel).size() : 0;
    }

    public int countOfSessions() {
        return channelSessions.values().stream().map(m -> m.size()).reduce(0, Integer::sum);
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_GLOBAL)
    public synchronized void onFanout(String ignored, Object data) {
        WsMessageEntity messageEntity = (WsMessageEntity) data;
        String channel = messageEntity.getFrom().getChannel();
        List<WsSession> sessions = getSessions(channel);
        if (CollectionUtils.isNotEmpty(sessions)) {
            sessions.stream().filter(session -> ArrayUtils.isEmpty(messageEntity.getIncludedSessionIds()) ||
                            ArrayUtils.contains(messageEntity.getIncludedSessionIds(), session.getUser().getSessionId()))
                    .forEach(session -> {
                    	stateChangeListenerContainer.triggerIfReceiveMessage(messageEntity.getFrom(), messageEntity.getPayload(),
                                messageEntity.getTimestamp(), session);
                    });
        }
    }
}