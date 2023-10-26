package io.doodler.ws;

import static io.doodler.ws.WsContants.CHANNEL_CHAT;
import static io.doodler.ws.WsContants.CHANNEL_USER;
import static io.doodler.ws.WsContants.PUBSUB_CHANNEL_FANOUT_ONE_USER;
import static io.doodler.ws.WsContants.PUBSUB_CHANNEL_FANOUT_OTHER_USERS;

import org.springframework.web.socket.WebSocketSession;

import io.doodler.common.context.InstanceId;
import io.doodler.redis.pubsub.RedisPubSubService;
import io.doodler.security.IdentifiableUserDetails;

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