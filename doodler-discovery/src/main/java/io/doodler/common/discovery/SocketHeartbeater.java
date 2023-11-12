package io.doodler.common.discovery;

import io.doodler.common.utils.NetUtils;

/**
 * @Description: SocketHeartbeater
 * @Author: Fred Feng
 * @Date: 06/04/2023
 * @Version 1.0.0
 */
public class SocketHeartbeater implements Heartbeater {

    private final int timeout;
    private final boolean usePublicIp;

    public SocketHeartbeater(int timeout) {
        this(timeout, false);
    }

    public SocketHeartbeater(int timeout, boolean usePublicIp) {
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