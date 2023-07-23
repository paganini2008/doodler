package io.doodler.discovery;

import static io.doodler.discovery.DiscoveryClientConstants.REDIS_KEY_PATTERN_DISCOVERY_CLIENT;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.doodler.common.enums.AppName;
import io.doodler.common.utils.JacksonUtils;
import io.doodler.common.utils.NetUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DiscoveryClientRegistrar implements SmartApplicationListener {

	private static final Class<?>[] sensitiveApplicationEventClasses = new Class[] { ApplicationReadyEvent.class,
			DiscoveryClientRefreshEvent.class };

	private final StringRedisTemplate redisOperations;

	private final PingStrategy pingStrategy;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private int port;

	@Value("${spring.application.weight:1}")
	private int weight;

	@Value("${spring.application.hostname:}")
	private String hostname;

	@Value("${spring.mvc.servlet.path}")
	private String contextPath;

	private final String localHost = NetUtils.getLocalHost();

	private final String publicIp = NetUtils.getPublicIp();

	public void registerSelf() {
		ApplicationInfo applicationInfo = new ApplicationInfo(applicationName, getHost(), publicIp, port, false, weight, contextPath);
		String key = String.format(REDIS_KEY_PATTERN_DISCOVERY_CLIENT, applicationName);
		String json = JacksonUtils.toJsonString(applicationInfo);
		Long index = redisOperations.opsForList().indexOf(key, json);
		if (index == null) {
			redisOperations.opsForList().leftPush(key, json);
			if (log.isInfoEnabled()) {
				log.info("Add application instance: '{}' to '{}'", applicationInfo, applicationName);
			}
		}
	}

	public void cleanExpiredSiblings() {
		String host = getHost();
		String key = String.format(REDIS_KEY_PATTERN_DISCOVERY_CLIENT, applicationName);
		List<String> list = redisOperations.opsForList().range(key, 0, -1);
		list.parallelStream().map(str -> JacksonUtils.parseJson(str, ApplicationInfo.class))
				.filter(info -> !(info.getHost().equals(host) && info.getPort() == port)).forEach(info -> {
					if (!pingStrategy.isAlive(info)) {
						String jsonString = JacksonUtils.toJsonString(info);
						redisOperations.opsForList().remove(key, 1, jsonString);
						if (log.isInfoEnabled()) {
							log.info("Remove expired application: {}", info);
						}
					}
				});
	}

	private String getHost() {
		String host = this.hostname;
		if (StringUtils.isBlank(host)) {
			host = this.localHost;
		}
		return host;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			AppName.get(applicationName);
		} catch (RuntimeException e) {
			return;
		}
		registerSelf();
		cleanExpiredSiblings();
	}

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return ArrayUtils.contains(sensitiveApplicationEventClasses, eventType);
	}
}