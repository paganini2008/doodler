package com.github.doodler.common.transmitter;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.utils.MutableObservable;
import com.github.doodler.common.utils.ThreadUtils;

/**
 * 
 * @Description: ConnectionWatcher
 * @Author: Fred Feng
 * @Date: 27/12/2024
 * @Version 1.0.0
 */
public final class ConnectionWatcher {

    private final int checkInterval;
    private final TimeUnit timeUnit;
    private final NioConnection connection;

    public ConnectionWatcher(int checkInterval, TimeUnit timeUnit, NioConnection connection) {
        this.checkInterval = checkInterval;
        this.timeUnit = timeUnit;
        this.connection = connection;
    }

    private final MutableObservable observable = new MutableObservable(false);

    public void reconnect(SocketAddress remoteAddress) {
        observable.notifyObservers(remoteAddress);
    }

    public void watch(final SocketAddress remoteAddress, final HandshakeCallback callback) {
        observable.addObserver((ob, arg) -> {
            do {
                ThreadUtils.sleep(checkInterval, timeUnit);
                connection.connect(remoteAddress, callback);
            } while (!connection.isConnected(remoteAddress));
        });
    }

}
