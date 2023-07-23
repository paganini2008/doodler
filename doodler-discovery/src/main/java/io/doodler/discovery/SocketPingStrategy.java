package io.doodler.discovery;

import io.doodler.common.utils.NetUtils;

/**
 * @Description: SocketPingStrategy
 * @Author: Fred Feng
 * @Date: 06/04/2023
 * @Version 1.0.0
 */
public class SocketPingStrategy implements PingStrategy {

    private final int timeout;
    private final boolean usePublicIp;

    public SocketPingStrategy(int timeout) {
        this(timeout, false);
    }

    public SocketPingStrategy(int timeout, boolean usePublicIp) {
        this.timeout = timeout;
        this.usePublicIp = usePublicIp;
    }

    @Override
    public boolean isAlive(ApplicationInfo info) {
        try {
            return NetUtils.canAccess(usePublicIp ? info.getPublicIp() : info.getHost(), info.getPort(), timeout);
        } catch (Exception e) {
            return false;
        }
    }
}