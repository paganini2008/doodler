package io.doodler.webmvc.actuator;

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

import io.doodler.common.Constants;
import io.doodler.common.utils.NetUtils;

/**
 * @Description: AdminClientEnvironmentPostProcessor
 * @Author: Fred Feng
 * @Date: 16/10/2023
 * @Version 1.0.0
 */
public class AdminClientEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	private static final String identifier = "adminClientEnvironment";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Map<String, Object> settings = new HashMap<>();
		settings.put("spring.boot.admin.client.instance.metadata.PID", getPid());
		settings.put("spring.boot.admin.client.instance.metadata.projectPath", System.getProperty("user.dir"));
		settings.put("spring.boot.admin.client.instance.metadata.jarFileName",
				String.format("%s-%s.jar", environment.getRequiredProperty("spring.application.name"), Constants.VERSION));
		settings.put("spring.boot.admin.client.instance.metadata.user", System.getProperty("user.name"));

		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		settings.put("spring.boot.admin.client.instance.metadata.vmArgs", StringUtils.join(arguments, " "));
		settings.put("spring.boot.admin.client.instance.metadata.extArgs", getDefaultExtArgs(environment));

		String localAddr = NetUtils.getLocalHost();
		String serviceUrl = String.format("http://%s:%s", localAddr,
				environment.getRequiredProperty("management.server.port"));
		settings.put("spring.boot.admin.client.instance.service-url", serviceUrl);

		environment.getPropertySources().addLast(new MapPropertySource(identifier, settings));
	}

	protected String getDefaultExtArgs(ConfigurableEnvironment environment) {
		String activeProfile = environment.getRequiredProperty("spring.profiles.active");
		return String.format("--spring.profiles.active=%s --randomServerPort", activeProfile);
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