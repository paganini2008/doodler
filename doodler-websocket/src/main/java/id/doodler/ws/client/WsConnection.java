package id.doodler.ws.client;

import java.util.Map;

import id.doodler.ws.DisposableHandler;
import io.doodler.common.utils.RetryQueue;

/**
 * @Description: WsConnection
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
public interface WsConnection extends WsClientRetriever {

    void setReconnector(Reconnector reconnector);

    void setRetryQueue(RetryQueue retryQueue);

    void setMaxIdleTime(long maxIdleTime);

    String getUrl();

    Map<String, String> getHttpHeaders();

    void addDisposableHandler(DisposableHandler handler);

    void addListeners(WsClientListener... listeners);

    void removeListener(WsClientListener listener);

    void connect(long timeout);
    
    String getSessionId();

    void close(boolean abandoned);

    boolean isClosed();

    boolean isAbandoned();

    boolean send(String text);
}