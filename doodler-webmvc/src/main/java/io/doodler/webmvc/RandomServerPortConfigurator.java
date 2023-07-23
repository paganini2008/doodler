package io.doodler.webmvc;

import static io.doodler.common.Constants.SERVER_PORT_END_WITH;
import static io.doodler.common.Constants.SERVER_PORT_START_WITH;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.doodler.common.utils.NetUtils;

/**
 * @Description: RandomServerPortConfigurator
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
public class RandomServerPortConfigurator implements EnvironmentPostProcessor {

	private static final String identifier = "randomServerPort";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		final Map<String, Object> settings = new HashMap<>();
		setServerPortIfAbsent(environment, settings);
		environment.getPropertySources().addFirst(new MapPropertySource(identifier, settings));
	}

	protected void setServerPortIfAbsent(ConfigurableEnvironment environment, Map<String, Object> settings) {
		if (environment.containsProperty("randomServerPort") || 
				!environment.containsProperty("server.port") || 
				StringUtils.isBlank(environment.getProperty("server.port"))) {
			int randomPort = NetUtils.getRandomPort(SERVER_PORT_START_WITH, SERVER_PORT_END_WITH);
			settings.put("server.port", randomPort);
		}
	}
}