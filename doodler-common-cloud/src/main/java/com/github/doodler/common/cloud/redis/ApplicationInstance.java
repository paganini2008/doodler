package com.github.doodler.common.cloud.redis;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cn.hutool.core.net.NetUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

/**
 * 
 * @Description: ApplicationInstance
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@JsonIgnoreProperties({"scheme", "uri"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class ApplicationInstance implements Cloneable, ServiceInstance {

    private String clusterId;

    private String instanceId;

    @EqualsAndHashCode.Include
    private String serviceId;

    @EqualsAndHashCode.Include
    private String host;

    @EqualsAndHashCode.Include
    private int port;

    private boolean secure;

    private int actuatorPort;

    private String externalHost;

    private String contextPath;

    private String actuatorContextPath;

    private Map<String, String> metadata;

    private int weight;

    private boolean registerSelf;

    @Override
    public URI getUri() {
        if (StringUtils.isNotBlank(host) && port > 0) {
            return DefaultServiceInstance.getUri(this);
        }
        return null;
    }

    @Override
    public String getScheme() {
        return secure ? "https" : "http";
    }

    @Override
    public String toString() {
        return String.format("[%s] serviceId: %s, host: %s, port: %d", instanceId, serviceId, host,
                port);
    }

    // public boolean isSibling(ServiceInstance other) {
    // return other.getServiceId().equals(getServiceId())
    // && (!other.getInstanceId().equals(getInstanceId())
    // || !other.getHost().equals(getHost()) || other.getPort() != getPort());
    // }

    @SneakyThrows
    public URI createUri(boolean usePublicIp, String... paths) {
        URI uri =
                URI.create(String.format("%s://%s", getScheme(), getHostString(usePublicIp, port)));
        if (ArrayUtils.isNotEmpty(paths)) {
            URL url = uri.toURL();
            url = new URL(url, String.join("/", paths));
            uri = url.toURI();
        }
        return uri;
    }

    @SneakyThrows
    public URI createActuatorUri(boolean usePublicIp, String... paths) {
        URI uri = URI.create(
                String.format("%s://%s", getScheme(), getHostString(usePublicIp, actuatorPort)));
        if (ArrayUtils.isNotEmpty(paths)) {
            URL url = uri.toURL();
            url = new URL(url, String.join("/", paths));
            uri = url.toURI();
        }
        return uri;
    }

    private String getHostString(boolean usePublicIp, int port) {
        String uri;
        if (usePublicIp) {
            uri = externalHost + ":" + port;
        } else {
            if ("localhost".equals(host) || "127.0.0.1".equals(host) || NetUtil.isInnerIP(host)) {
                uri = host + ":" + port;
            } else {
                uri = host;
            }
        }
        return uri;
    }

    @Override
    public ApplicationInstance clone() {
        try {
            return (ApplicationInstance) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    static ApplicationInstance fromRegistration(Registration registration) {
        ApplicationInstance instance = new ApplicationInstance();
        BeanUtils.copyProperties(registration, instance);
        return instance;
    }

}
