package com.github.doodler.common.discovery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import com.github.doodler.common.utils.NetUtils;

/**
 * @Description: ApplicationInfoHolder
 * @Author: Fred Feng
 * @Date: 17/08/2023
 * @Version 1.0.0
 */
public class ApplicationInfoHolder implements InitializingBean {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private int port;
    
    @Value("${management.server.port}")
    private int healthPort;

    @Value("${spring.application.weight:1}")
    private int weight;

    @Value("${spring.application.hostname:}")
    private String hostname;

    @Value("${spring.mvc.servlet.path}")
    private String contextPath;
    
    @Value("${management.endpoints.web.base-path}")
    private String healthContextPath;

    private final String localHost = NetUtils.getLocalHost();

    private final String publicIp = NetUtils.getPublicIp();

    private ApplicationInfo info;

    private @Nullable ApplicationInfo primary;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.info = new ApplicationInfo(applicationName, getHost(), publicIp, port, false, weight, contextPath,healthPort, healthContextPath);
    }

    public void setPrimary(ApplicationInfo primary) {
        this.primary = primary;
    }

    public ApplicationInfo getPrimary() {
        return primary;
    }

    public ApplicationInfo get() {
        return info;
    }

    public boolean isPrimary() {
        return primary != null && info.equals(primary);
    }

    private String getHost() {
        String host = this.hostname;
        if (StringUtils.isBlank(host)) {
            host = this.localHost;
        }
        return host;
    }
}