package com.github.doodler.common.ws;

import java.io.IOException;

/**
 * @Description: WsSession
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
public interface WsSession extends LifeCycle {

    WsUser getUser();

    void sendSessionId() throws IOException;

    void sendObject(Object data, long timestamp) throws IOException;

    void sendText(String text) throws IOException;

    void sendPing() throws IOException;

    void sendPong() throws IOException;

    void fanout(Object object, String... includedSessionIds);

    boolean isOpen();

    long getLastSentTimeMillis();

    void addDisposableHandler(DisposableHandler disposableHandler);
}