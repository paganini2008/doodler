package com.github.doodler.common.ws;

import static com.github.doodler.common.ws.WsContants.CHANNEL_CHAT;
import static com.github.doodler.common.ws.WsContants.CHANNEL_USER;
import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_ONE_USER;
import static com.github.doodler.common.ws.WsContants.PUBSUB_CHANNEL_FANOUT_OTHER_USERS;
import org.springframework.web.socket.WebSocketSession;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import com.github.doodler.common.security.IdentifiableUserDetails;

/**
 * @Description: BasicUserSession
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
public class BasicUserSession extends AnonymousUserSession {

    public BasicUserSession(WebSocketSession session,
                            String channel,
                            IdentifiableUserDetails userDetails,
                            InstanceId instanceId,
                            WsCodecFactory wsCodecFactory,
                            RedisPubSubService pubSubService) {
        super(new BasicUser(channel, session.getId(), userDetails), session, instanceId, wsCodecFactory,
                pubSubService);
    }

    @Override
    public void fanout(Object object, String... includedSessionIds) {
        WsUser user = getUser();
        WsMessageEntity messageEntity = new WsMessageEntity(instanceId.get(), user, object, includedSessionIds);
        switch (user.getChannel()) {
            case CHANNEL_USER:
                pubSubService.convertAndMulticast(PUBSUB_CHANNEL_FANOUT_ONE_USER, messageEntity);
                break;
            case CHANNEL_CHAT:
                pubSubService.convertAndMulticast(PUBSUB_CHANNEL_FANOUT_OTHER_USERS, messageEntity);
                break;
        }
    }
}