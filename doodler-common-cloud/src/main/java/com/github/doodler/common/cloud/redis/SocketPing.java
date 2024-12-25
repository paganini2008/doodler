package com.github.doodler.common.cloud.redis;

import org.springframework.cloud.client.ServiceInstance;
import com.github.doodler.common.utils.NetUtils;

/**
 * 
 * @Description: SocketPing
 * @Author: Fred Feng
 * @Date: 05/08/2024
 * @Version 1.0.0
 */
public class SocketPing implements Ping {

    private final int timeout;
    private final boolean usePublicIp;

    public SocketPing(int timeout) {
        this(timeout, false);
    }

    public SocketPing(int timeout, boolean usePublicIp) {
        this.timeout = timeout;
        this.usePublicIp = usePublicIp;
    }

    @Override
    public boolean isAlive(ServiceInstance instance) {
        try {
            return NetUtils
                    .canAccess(usePublicIp ? ((ApplicationInstance) instance).getExternalHost()
                            : instance.getHost(), instance.getPort(), timeout);
        } catch (Exception e) {
            return false;
        }
    }
}
