package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.SERVER_PORT_FROM;
import static com.github.doodler.common.Constants.SERVER_PORT_TO;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;
import com.github.doodler.common.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JmxConfig
 * @Author: Fred Feng
 * @Date: 05/03/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
@Slf4j
@Deprecated
@ConditionalOnProperty("jmx.enabled")
@Configuration
public class JmxConfig {

    @Value("${spring.jmx.hostName:}")
    private String rmiHostName;

    @Value("${spring.jmx.hostName:}")
    private Integer rmiPort;

    @PostConstruct
    public void configure() {
        if (StringUtils.isBlank(rmiHostName)) {
            rmiHostName = NetUtils.getLocalHostAddress();
        }
        if (rmiPort == null) {
            rmiPort = NetUtils.getRandomPort(SERVER_PORT_FROM, SERVER_PORT_TO);
        }
    }

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(rmiPort);
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean =
                new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        final String jmxServiceUrl =
                String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHostName,
                        rmiPort, rmiHostName, rmiPort);
        connectorServerFactoryBean.setServiceUrl(jmxServiceUrl);
        log.info("Jmx service url: {}", jmxServiceUrl);
        return connectorServerFactoryBean;
    }
}
