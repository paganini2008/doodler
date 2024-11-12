package com.github.doodler.common.cloud.redis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cloud.client.DefaultServiceInstance;
import com.github.doodler.common.utils.MapUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: RedisRegistration
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@Getter
@Setter
public class RedisRegistration implements ServiceRegistration {

    private String instanceId;
    private String applicationName;
    private String host;
    private int port;
    private boolean secure;
    private int actuatorPort;
    private String externalHost;
    private String contextPath;
    private String actuatorContextPath;
    private int weight;
    private boolean registerSelf;
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String getServiceId() {
        return applicationName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return MapUtils.obj2Map(this, false);
    }

}
