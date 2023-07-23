package io.doodler.cluster;

import java.io.Serializable;
import lombok.Data;

/**
 * @Description: Node
 * @Author: Fred Feng
 * @Date: 18/07/2023
 * @Version 1.0.0
 */
@Data
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = -2165854474149577160L;
    private String clusterName;
    private String applicationName;
    private String id;
    private String contextPath;
    private int weight;
    private String host;
    private String publicIp;
    private int port;
    private boolean secure;
    private String healthCheckUrl = "/ping";
    private long uptime;

    public ServiceInstance() {
        this.uptime = System.currentTimeMillis();
    }

    public ServiceInstance(String clusterName, String id) {
        this();
        this.clusterName = clusterName;
        this.id = id;
    }

    public String toHostUrl(boolean usePublicIp) {
        return String.format("%s://%s:%d", secure ? "https" : "http", usePublicIp ? publicIp : host, port);
    }
}