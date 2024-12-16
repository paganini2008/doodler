package com.github.doodler.common.webmvc.actuator;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import com.github.doodler.common.Constants;
import com.github.doodler.common.utils.NetUtils;

/**
 * @Description: AdminClientEnvironmentPostProcessor
 * @Author: Fred Feng
 * @Date: 16/10/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class AdminClientEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String identifier = "adminClientEnvironment";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        boolean hasPrefix = environment.getPropertySources().stream().filter(p -> p instanceof MapPropertySource).anyMatch(
                propertySource -> {
                    if (propertySource.getSource() instanceof Map) {
                        return ((Map) propertySource.getSource()).keySet().stream().anyMatch(
                                key -> key.toString().startsWith("spring.boot.admin.client"));
                    }
                    return false;
                });
        if (!hasPrefix) {
            return;
        }

        Map<String, Object> settings = new HashMap<>();
        settings.put("spring.boot.admin.client.username", "admin");
        settings.put("spring.boot.admin.client.password", "admin123");

        settings.put("spring.boot.admin.client.instance.metadata.PID", getPid());
        settings.put("spring.boot.admin.client.instance.metadata.projectPath",
                System.getProperty("user.dir"));
        settings.put("spring.boot.admin.client.instance.metadata.jarFileName",
                String.format("%s-%s.jar",
                        environment.getRequiredProperty("spring.application.name"),
                        Constants.VERSION));
        settings.put("spring.boot.admin.client.instance.metadata.user",
                System.getProperty("user.name"));

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        settings.put("spring.boot.admin.client.instance.metadata.vmArgs",
                StringUtils.join(arguments, " "));
        settings.put("spring.boot.admin.client.instance.metadata.extArgs",
                getDefaultExtArgs(environment));

        String localAddr = NetUtils.getLocalHostAddress();
        String serviceUrl = String.format("http://%s:%s", localAddr,
                environment.getRequiredProperty("management.server.port"));
        settings.put("spring.boot.admin.client.instance.service-url", serviceUrl);

        environment.getPropertySources().addLast(new MapPropertySource(identifier, settings));
    }

    protected String getDefaultExtArgs(ConfigurableEnvironment environment) {
        String applicationName = environment.getRequiredProperty("spring.application.name");
        String activeProfile = environment.getRequiredProperty("spring.profiles.active");
        String realm = environment.getProperty("spring.application.realm", "kos");
        String extArgs = "--spring.profiles.active=%s --spring.application.realm=%s";
        if ("crypto-alert-service".equals(applicationName)) {

        } else {
            extArgs += " --spring.config.additional-location=file:/data/app/home/%s-backend/realm/%s/application-%s.yml";
            if ("crypto-chat-service".equals(applicationName) || "crypto-newsletter-service".equals(applicationName) ||
                    "crypto-job-service".equals(applicationName)) {
                int port = environment.getRequiredProperty("server.port", int.class);
                extArgs += " --server.port=" + port;
            } else {
                extArgs += " --randomServerPort";
            }
        }
        return String.format(extArgs, activeProfile, realm, applicationName, realm, activeProfile);
    }

    private String getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
