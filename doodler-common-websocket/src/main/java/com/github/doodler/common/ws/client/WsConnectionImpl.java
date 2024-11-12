package com.github.doodler.common.ws.client;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.java_websocket.enums.ReadyState;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.retry.RetryQueue;
import com.github.doodler.common.retry.SimpleRetryQueue;
import com.github.doodler.common.utils.JacksonUtils;
import com.github.doodler.common.utils.MutableObservable;
import com.github.doodler.common.ws.DisposableHandler;
import com.github.doodler.common.ws.WsContants;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WsConnectionImpl
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class WsConnectionImpl implements WsClientListener, WsConnection {

    private final Map<String, String> httpHeaders;
    private final MutableObservable observable = new MutableObservable(true);

    public WsConnectionImpl(String url, Map<String, String> httpHeaders) {
        this.connection = new WsClientWrapper(URI.create(url), httpHeaders, this);
        this.httpHeaders = httpHeaders;
    }

    private WsClientWrapper connection;
    private IdleStateChecker idleStateChecker;
    private Reconnector reconnector = new EagerReconnector(5000L);
    private RetryQueue retryQueue = new SimpleRetryQueue();
    private final AtomicBoolean abandoned = new AtomicBoolean();
    private final AtomicBoolean connecting = new AtomicBoolean();
    private long timeout;
    private String sessionId;
    private CountDownLatch countDownLatch;

    @Override
    public void connect(long timeout) {
        checkAbandoned();
        if (connecting.get()) {
            if (log.isWarnEnabled()) {
                log.warn("WsConnection is connecting now.");
            }
            return;
        }
        if (connection.isOpen()) {
            if (log.isInfoEnabled()) {
                log.info("WsConnection has been connected successfully now.");
            }
            return;
        }
        connecting.set(true);
        connection.connect();
        countDownLatch = new CountDownLatch(1);
        try {
			countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ignored) {
		}
        this.timeout = timeout;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void close(boolean abandoned) {
        checkAbandoned();
        this.abandoned.set(abandoned);
        if (connection.isOpen()) {
            connection.close();
        }
        if (isAbandoned()) {
            if (idleStateChecker != null) {
                idleStateChecker.stop();
            }
        }
    }

    @Override
    public boolean isClosed() {
        return connection.getReadyState() != ReadyState.OPEN;
    }

    @Override
    public boolean isAbandoned() {
        return abandoned.get();
    }

    private void checkAbandoned() {
        if (isAbandoned()) {
            throw new IllegalStateException("WsConnection is abandoned");
        }
    }

    @Override
    public synchronized void retrieve() {
        checkAbandoned();
        if (connection != null && isClosed()) {
            this.connection = new WsClientWrapper(connection.getURI(), httpHeaders, this);
            connect(timeout);
            if (log.isInfoEnabled()) {
                log.info("Renewed WsConnection: {}", this.connection);
            }
        }
    }

    @Override
    public boolean send(String text) {
        checkAbandoned();
        synchronized (this) {
            if (reconnector.shouldReconnect(this)) {
                reconnector.onSending(this);
            }
        }
        if (connection.isOpen()) {
            connection.send(text);
            if (idleStateChecker != null) {
                idleStateChecker.setLastSentTime(System.currentTimeMillis());
            }
            return true;
        } else {
            retryQueue.putObject(text);
            return false;
        }
    }

    @Override
    public void setReconnector(Reconnector reconnector) {
        this.reconnector = reconnector;
    }

    @Override
    public void setRetryQueue(RetryQueue retryQueue) {
        this.retryQueue = retryQueue;
    }

    @Override
    public void setMaxIdleTime(long maxIdleTime) {
        this.idleStateChecker = new IdleStateChecker(this, maxIdleTime);
    }

    @Override
    public String getUrl() {
        return connection.getURI().toString();
    }

    @Override
    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public void addDisposableHandler(DisposableHandler handler) {
        observable.addObserver("disposable", (ob, arg) -> {
            handler.dispose(arg);
        });
    }

    @Override
    public void addListeners(WsClientListener... clientListeners) {
        connection.addListeners(clientListeners);
    }

    @Override
    public void removeListener(WsClientListener clientListener) {
        connection.removeListener(clientListener);
    }

    @Override
    public void onOpen(int status, String message) {
        if (log.isInfoEnabled()) {
            log.info("WsConnnection is open. Status: {}, Message: {}", status,
                    message);
        }
        connecting.set(false);
        reconnector.reset();
        
        if (retryQueue.size() > 0) {
            Deque<Object> q = new ArrayDeque<>();
            retryQueue.drainTo(q);
            while (!q.isEmpty()) {
                send((String) q.poll());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setSessionId(String message) {
    	if(StringUtils.isNotBlank(sessionId)) {
    		return;
    	}
        ApiResult<String> apiResult;
        try {
            apiResult = JacksonUtils.parseJson(message, ApiResult.class);
        } catch (Exception e) {
            return;
        }
        String data = apiResult.getData();
        if (StringUtils.isNotBlank(data) && data.startsWith(WsContants.SESSION_ID_PREFIX)) {
            this.sessionId = data;
        }
        countDownLatch.countDown();
    }

    @Override
	public void onMessage(String msg) {
    	if(log.isTraceEnabled()) {
    		log.trace("Accept message: {}",msg);
    	}
    	setSessionId(msg);
	}

	@Override
    public void onClose(int code, String reason, boolean remote) {
        if (log.isInfoEnabled()) {
            log.info("WsConnnection is closed. Code: {}, Reason: {}, Remote: {}", code, reason, remote);
        }
        connecting.set(false);

        if (isAbandoned()) {
            observable.notifyObservers("disposable", this);
        } else {
            if (reconnector.shouldReconnect(this)) {
                reconnector.onClosing(this);
            }
        }
    }

    @Override
    public void onError(Exception e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        connecting.set(false);

        if (isAbandoned()) {
            observable.notifyObservers("disposable", this);
        } else {
            if (reconnector.shouldReconnect(this)) {
                reconnector.onError(this);
            }
        }
    }
}