package com.github.doodler.common.ws.handler;

import com.github.doodler.common.redis.pubsub.RedisPubSub;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.ws.WsMessageEntity;
import com.github.doodler.common.ws.WsSession;
import com.github.doodler.common.ws.WsSessionFilter;
import com.github.doodler.common.ws.WsStateChangeListenerContainer;
import com.github.doodler.common.ws.WsUser;
import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_ONE_USER;
import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_OTHER_USERS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @Description: UserSessionContext
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class UserSessionContext implements ApplicationListener<ApplicationReadyEvent> {

    private final Map<String, Map<WsUser, WsSession>> channelSessions = new ConcurrentHashMap<>();
    private final WsStateChangeListenerContainer stateChangeListenerContainer;
    private final List<WsSessionFilter> sessionFilters = new CopyOnWriteArrayList<>();

    public void addSession(String channel, WsUser user, WsSession wsSession) {
        Map<WsUser, WsSession> map = MapUtils.getOrCreate(channelSessions, channel, ConcurrentHashMap::new);
        if (user != null) {
            map.put(user, wsSession);
        }
    }

    public WsSession removeSession(String channel, WsUser user) {
        WsSession session = null;
        Map<WsUser, WsSession> map = channelSessions.get(channel);
        if (map != null && user != null) {
            session = map.remove(user);
        }
        if (MapUtils.isEmpty(map)) {
            channelSessions.remove(channel);
        }
        return session;
    }

    public List<WsSession> getSessions(String channel) {
        Map<WsUser, WsSession> handlers = channelSessions.get(channel);
        if (MapUtils.isEmpty(handlers)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(handlers.values());
    }

    public List<WsSession> getAllWsSessions() {
        List<WsSession> sessions = new ArrayList<>();
        channelSessions.values().forEach(m -> {
            sessions.addAll(m.values());
        });
        return sessions;
    }

    public List<WsSession> getSessions(String channel, Long userId) {
        return getSessions(channel, session -> {
            if (session.getUser().getUserDetails() == null) {
                return false;
            }
            return session.getUser().getUserDetails().getId().equals(userId);
        });
    }

    public List<WsSession> getSessions(String channel, Predicate<WsSession> filter) {
        Map<WsUser, WsSession> handlers = channelSessions.get(channel);
        if (MapUtils.isEmpty(handlers)) {
            return Collections.emptyList();
        }
        if (filter != null) {
            return handlers.values().stream().filter(filter).collect(Collectors.toList());
        }
        return new ArrayList<>(handlers.values());
    }

    public WsSession getSession(String channel, WsUser user) {
        Map<WsUser, WsSession> map = channelSessions.get(channel);
        return map != null && map.containsKey(user) ? map.get(user) : null;
    }

    public int countOfSessions(String channel) {
        return channelSessions.containsKey(channel) ? channelSessions.get(channel).size() : 0;
    }

    public int countOfSessions() {
        return channelSessions.values().stream().map(m -> m.size()).reduce(0, Integer::sum);
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_ONE_USER)
    public synchronized void onFanoutOneUser(String channel, Object data) {
        WsMessageEntity messageEntity = (WsMessageEntity) data;
        WsUser user = messageEntity.getFrom();
        List<WsSession> sessions = getSessions(user.getChannel(), user.getUserDetails().getId());
        if (CollectionUtils.isNotEmpty(sessions)) {
            sessions.stream()
                    .filter(session -> ArrayUtils.isEmpty(messageEntity.getIncludedSessionIds())
                            || ArrayUtils.contains(messageEntity.getIncludedSessionIds(), session.getUser().getSessionId()))
                    .forEach(session -> {
                        stateChangeListenerContainer.triggerIfReceiveMessage(messageEntity.getFrom(),
                                messageEntity.getPayload(),
                                messageEntity.getTimestamp(), session);
                    });
        }
    }

    @RedisPubSub(PUBSUB_CHANNEL_FANOUT_OTHER_USERS)
    public synchronized void onFanoutOtherUsers(String channel, Object data) {
        WsMessageEntity messageEntity = (WsMessageEntity) data;
        WsUser user = messageEntity.getFrom();
        List<WsSession> allSessions = getSessions(user.getChannel());
        if (CollectionUtils.isNotEmpty(allSessions)) {
            List<WsSession> reference = allSessions;
            for (WsSessionFilter sessionFilter : sessionFilters) {
                if (sessionFilter.shouldFilter(reference, messageEntity.getPayload())) {
                    reference = sessionFilter.filter(reference, messageEntity.getPayload());
                }
            }
            List<WsSession> wsSessions = reference.stream()
                    .filter(session -> ArrayUtils.isEmpty(messageEntity.getIncludedSessionIds())
                            || ArrayUtils.contains(messageEntity.getIncludedSessionIds(), session.getUser().getSessionId()))
                    .collect(Collectors.toList());
            wsSessions.forEach(session -> {
                stateChangeListenerContainer.triggerIfReceiveMessage(messageEntity.getFrom(), messageEntity.getPayload(),
                        messageEntity.getTimestamp(), session);
            });
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        Map<String, WsSessionFilter> filterBeans = event.getApplicationContext().getBeansOfType(WsSessionFilter.class);
        if (MapUtils.isNotEmpty(filterBeans)) {
            sessionFilters.addAll(filterBeans.values());
            sessionFilters.sort(AnnotationAwareOrderComparator.INSTANCE);
        }
    }
}