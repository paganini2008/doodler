package io.doodler.common.discovery;

import io.doodler.common.discovery.AffectedApplicationInfo.AffectedType;
import io.doodler.common.utils.ExecutorUtils;
import io.doodler.common.utils.MapUtils;
import io.doodler.common.utils.SimpleTimer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @Description: HeartbeatChecker
 * @Author: Fred Feng
 * @Date: 02/09/2023
 * @Version 1.0.0
 */
public abstract class HeartbeatChecker extends SimpleTimer implements ApplicationEventPublisherAware {

    private final Heartbeater heartbeater;

    public HeartbeatChecker(long initialDelay, long checkInterval, Heartbeater heartbeater) {
        super(initialDelay, checkInterval, TimeUnit.SECONDS);
        this.heartbeater = heartbeater;
    }

    @Getter
    private volatile Map<String, Collection<ApplicationInfo>> latestSnapshots;

    private ApplicationEventPublisher applicationEventPublisher;

    @Setter
    private ExecutorService executor;

    private boolean cascadeClosed;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.latestSnapshots = initialize();
        if (executor == null) {
            this.executor = Executors.newFixedThreadPool(10);
            this.cascadeClosed = true;
        }
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        if (executor != null && cascadeClosed) {
            ExecutorUtils.gracefulShutdown(executor, 60000L);
            executor = null;
        }
    }

    public boolean checkAlive(ApplicationInfo info) {
        return heartbeater.isAlive(info);
    }

    @Override
    public boolean change() throws Exception {
        Map<String, Collection<ApplicationInfo>> snapshots = fetchApplicationInfos();
        checkRemovals(snapshots);
        List<AffectedApplicationInfo> affects = hasChanged(snapshots);
        latestSnapshots = snapshots;
        if (CollectionUtils.isNotEmpty(affects)) {
            if (log.isInfoEnabled()) {
                for (AffectedApplicationInfo info : affects) {
                    log.info("{}", info.toString());
                }
            }
            handleAffectedApplicationInfos(affects, applicationEventPublisher);
        }
        return true;
    }

    protected abstract Map<String, Collection<ApplicationInfo>> initialize();

    protected abstract Map<String, Collection<ApplicationInfo>> fetchApplicationInfos();

    protected abstract void handleAffectedApplicationInfos(Collection<AffectedApplicationInfo> affects,
                                                           ApplicationEventPublisher applicationEventPublisher);

    private void checkRemovals(Map<String, Collection<ApplicationInfo>> snapshots) {
        if (MapUtils.isNotEmpty(snapshots)) {
            try {
                List<Callable<List<ApplicationInfo>>> callables = snapshots.keySet().stream()
                        .map(applicationName -> getCallable(applicationName, snapshots)).collect(Collectors.toList());
                ExecutorUtils.submitAll(executor, callables, removals -> {
                    if (log.isTraceEnabled()) {
                        if (CollectionUtils.isNotEmpty(removals)) {
                            for (ApplicationInfo info : removals) {
                                log.trace("Remove application info: {}", info.toString());
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

    private Callable<List<ApplicationInfo>> getCallable(String applicationName,
                                                        Map<String, Collection<ApplicationInfo>> infos) {
        return () -> {
            List<ApplicationInfo> removals = new ArrayList<>();
            Collection<ApplicationInfo> candidates = infos.get(applicationName);
            for (ApplicationInfo candidate : candidates) {
                if (!checkAlive(candidate)) {
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

    private AffectedType getAffectedType(String affectedApplicationName,
                                         Map<String, Collection<ApplicationInfo>> snapshots) {
        if (latestSnapshots.containsKey(affectedApplicationName) && !snapshots.containsKey(affectedApplicationName)) {
            return AffectedType.OFFLINE;
        } else if (!latestSnapshots.containsKey(affectedApplicationName) &&
                snapshots.containsKey(affectedApplicationName)) {
            return AffectedType.ONLINE;
        }
        return AffectedType.NONE;
    }

    private AffectedType getAffectedType(ApplicationInfo affectedApplicationInfo,
                                         Collection<ApplicationInfo> lastApplicationInfos,
                                         Collection<ApplicationInfo> applicationInfos) {
        if (lastApplicationInfos.contains(affectedApplicationInfo) && !applicationInfos.contains(affectedApplicationInfo)) {
            return AffectedType.OFFLINE;
        } else if (!lastApplicationInfos.contains(affectedApplicationInfo) &&
                applicationInfos.contains(affectedApplicationInfo)) {
            return AffectedType.ONLINE;
        }
        return AffectedType.NONE;
    }

    private List<AffectedApplicationInfo> hasChanged(Map<String, Collection<ApplicationInfo>> snapshots) {
        List<AffectedApplicationInfo> affected = new ArrayList<>();
        Set<String> applicationNames = snapshots.keySet();
        Set<String> lastApplicationNames =
                MapUtils.isNotEmpty(latestSnapshots) ? latestSnapshots.keySet() : Collections.emptySet();
        Collection<String> affectedApplicationNames = CollectionUtils.disjunction(applicationNames, lastApplicationNames);
        if (CollectionUtils.isNotEmpty(affectedApplicationNames)) {
            Collection<ApplicationInfo> set = Collections.emptySet();
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
        Collection<ApplicationInfo> applicationInfos;
        Collection<ApplicationInfo> lastApplicationInfos;
        for (Map.Entry<String, Collection<ApplicationInfo>> entry : snapshots.entrySet()) {
            applicationName = entry.getKey();
            applicationInfos = entry.getValue();
            lastApplicationInfos = latestSnapshots.containsKey(applicationName) ? latestSnapshots.get(applicationName)
                    : Collections.emptySet();
            Collection<ApplicationInfo> affectedApplicationInfos = CollectionUtils.disjunction(applicationInfos,
                    lastApplicationInfos);
            if (CollectionUtils.isNotEmpty(affectedApplicationInfos)) {
                for (ApplicationInfo applicationInfo : affectedApplicationInfos) {
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
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}