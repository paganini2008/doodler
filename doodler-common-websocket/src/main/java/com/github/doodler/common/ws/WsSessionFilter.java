package com.github.doodler.common.ws;

import java.util.List;

/**
 * @Description: WsSessionFilter
 * @Author: Fred Feng
 * @Date: 10/03/2023
 * @Version 1.0.0
 */
public interface WsSessionFilter {

    default boolean shouldFilter(List<WsSession> sessionList, Object payload) {
        return true;
    }

    List<WsSession> filter(List<WsSession> sessionList, Object payload);
}