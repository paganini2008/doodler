package com.github.doodler.common.ws;

/**
 * @Description: WsContants
 * @Author: Fred Feng
 * @Date: 08/03/2021
 * @Version 1.0.0
 */
public interface WsContants {

    String SESSION_ID_PREFIX = "sid-";

    String CHANNEL_WEBSITE = "website";

    String CHANNEL_USER = "user";

    String CHANNEL_CHAT = "chat";

    String CONTEXT_PATH = "/news";

    String PUBSUB_CHANNEL_FANOUT_GLOBAL = "FANOUT-GLOBAL";

    String PUBSUB_CHANNEL_FANOUT_ONE_USER = "FANOUT-ONE-USER";

    String PUBSUB_CHANNEL_FANOUT_OTHER_USERS = "FANOUT-OTHER-USERS";
}