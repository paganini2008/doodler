package com.github.doodler.common.cloud;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.lang.Nullable;
import com.github.doodler.common.context.ContextPath;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.utils.NetUtils;
import lombok.Setter;

/**
 * @Description: ApplicationInfoHolder
 * @Author: Fred Feng
 * @Date: 17/08/2023
 * @Version 1.0.0
 */
public class ApplicationInfoHolder implements InitializingBean {

    @Value("${spring.application.weight:1}")
    private int weight;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.hostname:}")
    private String hostName;

    @Autowired
    private InstanceId instanceId;

    @Autowired
    private ContextPath contextPath;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private ManagementServerProperties managementServerProperties;

    private ApplicationInfo info;

    @Setter
    private @Nullable ApplicationInfo primary;

    @Setter
    private @Nullable Map<String, String> metadata;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.info = new ApplicationInfo(instanceId.get(), applicationName, getHostAddress(),
                NetUtils.getExternalIp(), serverProperties.getPort(),
                serverProperties.getSsl() != null ? serverProperties.getSsl().isEnabled() : false,
                weight, contextPath.getContextPath(),
                managementServerProperties.getPort() != null
                        ? managementServerProperties.getPort().intValue()
                        : 0,
                managementServerProperties.getBasePath(), metadata);
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

    private String getHostAddress() {
        String hostAddress = hostName;
        if (StringUtils.isBlank(hostAddress)) {
            if (serverProperties.getAddress() != null) {
                hostAddress = serverProperties.getAddress().getHostAddress();
            }
            if (StringUtils.isBlank(hostAddress)) {
                hostAddress = NetUtils.getLocalHostAddress();
            }
        }
        return hostAddress;
    }
}
