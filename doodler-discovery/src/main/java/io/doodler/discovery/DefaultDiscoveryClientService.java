package io.doodler.discovery;

import static io.doodler.discovery.DiscoveryClientConstants.REDIS_KEY_PREFIX_DISCOVERY_CLIENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import io.doodler.common.context.ManagedBeanLifeCycle;
import io.doodler.common.enums.AppName;
import io.doodler.common.utils.ExecutorUtils;
import io.doodler.common.utils.JacksonUtils;
import io.doodler.common.utils.MapUtils;
import io.doodler.discovery.AffectedApplicationInfo.AffectedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DiscoveryClientService
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultDiscoveryClientService implements ManagedBeanLifeCycle, ApplicationEventPublisherAware, DiscoveryClientService {

	private final StringRedisTemplate redisOperations;

	private final PingStrategy pingStrategy;

	private ApplicationEventPublisher applicationEventPublisher;

	private volatile Map<String, Set<ApplicationInfo>> latestSnapshots;

	@Value("${spring.application.name}")
	private String applicationName;

	private ExecutorService internalExecutor;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.latestSnapshots = getSnapshots();
		this.internalExecutor = Executors.newFixedThreadPool(AppName.values().length);
	}

	@Override
	public Set<ApplicationInfo> getApplicationInfos(String applicationName) {
		if (!latestSnapshots.containsKey(applicationName)) {
			return Collections.emptySet();
		}
		return latestSnapshots.get(applicationName);
	}

	@Override
	public Map<String, Set<ApplicationInfo>> getApplicationInfos() {
		return Collections.unmodifiableMap(latestSnapshots);
	}

	private Map<String, Set<ApplicationInfo>> getSnapshots() {
		Map<String, Set<ApplicationInfo>> map = new ConcurrentHashMap<>();
		Set<String> keys = redisOperations.keys(REDIS_KEY_PREFIX_DISCOVERY_CLIENT + "*");
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}
		keys.stream().flatMap(key -> redisOperations.opsForList().range(key, 0, -1).stream())
				.map(data -> JacksonUtils.parseJson(data, ApplicationInfo.class))
				.filter(info -> !info.getApplicationName().equals(applicationName)).forEach(info -> {
					Set<ApplicationInfo> instances = MapUtils.getOrCreate(map, info.getApplicationName(), CopyOnWriteArraySet::new);
					instances.add(info);
				});
		return map;
	}

	@Scheduled(initialDelay = 60, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
	public void runTask() {
		Map<String, Set<ApplicationInfo>> snapshots = getSnapshots();
		checkRemovals(snapshots);
		List<AffectedApplicationInfo> affects = hasChanged(snapshots);
		latestSnapshots = snapshots;
		if (CollectionUtils.isNotEmpty(affects)) {
			if (log.isInfoEnabled()) {
				for (AffectedApplicationInfo info : affects) {
					log.info(info.toString());
				}
			}
			applicationEventPublisher.publishEvent(new DiscoveryClientChangeEvent(this, affects));
		}
	}

	private void checkRemovals(Map<String, Set<ApplicationInfo>> snapshots) {
		if (MapUtils.isNotEmpty(snapshots)) {
			try {
				List<Callable<List<ApplicationInfo>>> callables = snapshots.keySet().stream()
						.map(applicationName -> getCallable(applicationName, snapshots)).collect(Collectors.toList());
				ExecutorUtils.submitAll(internalExecutor, callables, removals -> {
					if (log.isTraceEnabled()) {
						if (CollectionUtils.isNotEmpty(removals)) {
							for (ApplicationInfo info : removals) {
								log.trace("Remove ApplicationInfo: {}", info);
							}
						}
					}
				});
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private Callable<List<ApplicationInfo>> getCallable(String applicationName, Map<String, Set<ApplicationInfo>> infos) {
		return () -> {
			List<ApplicationInfo> removals = new ArrayList<>();
			Set<ApplicationInfo> candidates = infos.get(applicationName);
			for (ApplicationInfo candidate : candidates) {
				if (!pingStrategy.isAlive(candidate)) {
					candidates.remove(candidate);
					removals.add(candidate);
				}
			}
			if (candidates.isEmpty()) {
				infos.remove(applicationName);
			}
			return removals;
		};
	}

	private AffectedType getAffectedType(String affectedApplicationName, Map<String, Set<ApplicationInfo>> snapshots) {
		if (latestSnapshots.containsKey(affectedApplicationName) && !snapshots.containsKey(affectedApplicationName)) {
			return AffectedType.OFFLINE;
		} else if (!latestSnapshots.containsKey(affectedApplicationName) && snapshots.containsKey(affectedApplicationName)) {
			return AffectedType.ONLINE;
		}
		return AffectedType.NONE;
	}

	private AffectedType getAffectedType(ApplicationInfo affectedApplicationInfo, Set<ApplicationInfo> lastApplicationInfos,
			Set<ApplicationInfo> applicationInfos) {
		if (lastApplicationInfos.contains(affectedApplicationInfo) && !applicationInfos.contains(affectedApplicationInfo)) {
			return AffectedType.OFFLINE;
		} else if (!lastApplicationInfos.contains(affectedApplicationInfo) && applicationInfos.contains(affectedApplicationInfo)) {
			return AffectedType.ONLINE;
		}
		return AffectedType.NONE;
	}

	private List<AffectedApplicationInfo> hasChanged(Map<String, Set<ApplicationInfo>> snapshots) {
		List<AffectedApplicationInfo> affected = new ArrayList<>();
		Set<String> applicationNames = snapshots.keySet();
		Set<String> lastApplicationNames = MapUtils.isNotEmpty(latestSnapshots) ? latestSnapshots.keySet() : Collections.emptySet();
		Collection<String> affectedApplicationNames = CollectionUtils.disjunction(applicationNames, lastApplicationNames);
		if (CollectionUtils.isNotEmpty(affectedApplicationNames)) {
			Set<ApplicationInfo> set = Collections.emptySet();
			AffectedType affectedType;
			for (String affectedApplicationName : affectedApplicationNames) {
				affectedType = getAffectedType(affectedApplicationName, snapshots);
				if (affectedType == AffectedType.OFFLINE) {
					set = latestSnapshots.get(affectedApplicationName);
				} else if (affectedType == AffectedType.ONLINE) {
					set = snapshots.get(affectedApplicationName);
				}
				for (ApplicationInfo info : set) {
					AffectedApplicationInfo affectedInfo = new AffectedApplicationInfo();
					affectedInfo.setAffectedType(affectedType);
					affectedInfo.setApplicationInfo(info);
					affected.add(affectedInfo);
				}
			}
			return affected;
		}
		String applicationName;
		Set<ApplicationInfo> applicationInfos;
		Set<ApplicationInfo> lastApplicationInfos;
		for (Map.Entry<String, Set<ApplicationInfo>> entry : snapshots.entrySet()) {
			applicationName = entry.getKey();
			applicationInfos = entry.getValue();
			lastApplicationInfos = latestSnapshots.containsKey(applicationName) ? latestSnapshots.get(applicationName)
					: Collections.emptySet();
			Collection<ApplicationInfo> affectedApplicationInfos = CollectionUtils.disjunction(applicationInfos, lastApplicationInfos);
			if (CollectionUtils.isNotEmpty(affectedApplicationInfos)) {
				for(ApplicationInfo applicationInfo: affectedApplicationInfos) {
					AffectedApplicationInfo affectedInfo = new AffectedApplicationInfo();
					affectedInfo.setAffectedType(getAffectedType(applicationInfo, lastApplicationInfos, applicationInfos));
					affectedInfo.setApplicationInfo(applicationInfo);
					affected.add(affectedInfo);
				}
			}
		}
		return affected;
	}

	@Override
	public void destroy() throws Exception {
		if (internalExecutor != null) {
			ExecutorUtils.gracefulShutdown(internalExecutor, 60000L);
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
}