package io.doodler.discovery;

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
public class ApplicationInfo implements Serializable {

    private static final long serialVersionUID = 5025375997544999194L;

    private String applicationName;

    private String host;

    private String publicIp;

    private int port;

    private boolean secure;

    private int weight = 1;

    private String contextPath;

    public String toHostUrl(boolean usePublicIp) {
        return String.format("%s://%s:%d", secure ? "https" : "http", usePublicIp ? publicIp : host, port);
    }
}