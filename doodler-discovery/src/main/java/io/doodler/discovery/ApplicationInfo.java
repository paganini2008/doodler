package io.doodler.discovery;

import cn.hutool.core.net.NetUtil;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: ApplicationInfo
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = 5025375997544999194L;

    private String applicationName;

    private String host;

    private String publicIp;

    private int port;

    private boolean secure;

    private int weight = 1;

    private String contextPath;

    public boolean hasSibling(ApplicationInfo other) {
        return other.getApplicationName().equals(getApplicationName()) && (!other.getHost().equals(getHost()) ||
                other.getPort() != getPort());
    }

    public String toHostUrl(boolean usePublicIp) {
        String protocal = "http";
        if (secure) {
            protocal = "https";
        }
        String instance;
        if (usePublicIp) {
            instance = publicIp + ":" + port;
        } else {
            if ("localhost".equals(host) || "127.0.0.1".equals(host) || NetUtil.isInnerIP(host)) {
                instance = host + ":" + port;
            } else {
                instance = host;
            }
        }
        return String.format("%s://%s", protocal, instance);
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