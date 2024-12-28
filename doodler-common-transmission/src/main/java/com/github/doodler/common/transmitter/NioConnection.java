package com.github.doodler.common.transmitter;

import java.net.SocketAddress;

/**
 * 
 * @Description: NioConnection
 * @Author: Fred Feng
 * @Date: 27/12/2024
 * @Version 1.0.0
 */
public interface NioConnection {

    void connect(SocketAddress remoteAddress, HandshakeCallback handshakeCallback);

    boolean isConnected(SocketAddress remoteAddress);

}
