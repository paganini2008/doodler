package com.github.doodler.common.cloud;

import java.io.Serializable;
import com.github.doodler.common.utils.NetUtils;
import cn.hutool.core.net.NetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @Description: ApplicationInfo
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = 5025375997544999194L;

    private String instanceId;

    private String serviceId;

    private String host;

    private String externalHost;

    private int port;

    private boolean secure;

    private int weight = 1;

    private String contextPath;

    private int actuatorPort;

    private String actuatorContextPath;

    public String getApplicationName() {
        return serviceId;
    }

    public boolean isSibling(ApplicationInfo other) {
        return other.getServiceId().equals(getServiceId())
                && (!other.getInstanceId().equals(getInstanceId())
                        || !other.getHost().equals(getHost()) || other.getPort() != getPort());
    }

    public String getSchema() {
        return secure ? "https" : "http";
    }

    public String retriveHostUrl(boolean external) {
        String location;
        if (external) {
            location = externalHost + ":" + port;
        } else {
            if (NetUtils.isLoopbackAddress(host) || NetUtil.isInnerIP(host)) {
                location = host + ":" + port;
            } else {
                location = host;
            }
        }
        return String.format("%s://%s", getSchema(), location);
    }

    public String retriveActuatorHostUrl(boolean external) {
        String location;
        if (external) {
            location = externalHost + ":" + actuatorPort;
        } else {
            if (NetUtils.isLoopbackAddress(host) || NetUtil.isInnerIP(host)) {
                location = host + ":" + actuatorPort;
            } else {
                location = host;
            }
        }
        return String.format("%s://%s", getSchema(), location);
    }

    @Override
    public ApplicationInfo clone() {
        try {
            return (ApplicationInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
