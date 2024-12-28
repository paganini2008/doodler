package com.github.doodler.common.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import com.github.doodler.common.cloud.AffectedApplicationInfo.AffectedType;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;
import lombok.Getter;

/**
 * 
 * @Description: DiscoveryClientChecker
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
public abstract class DiscoveryClientChecker extends SimpleTimer
        implements ApplicationEventPublisherAware {

    public DiscoveryClientChecker(long initialDelay, long checkInterval, boolean quickStart) {
        super(initialDelay, checkInterval, TimeUnit.SECONDS, quickStart);
    }

    @Getter
    private volatile Map<String, Collection<ApplicationInfo>> latestSnapshots;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.latestSnapshots = initialize();
    }

    @Override
    public final boolean change() throws Exception {
        Map<String, Collection<ApplicationInfo>> snapshots = fetchApplicationInfos();
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

    protected abstract void handleAffectedApplicationInfos(
            Collection<AffectedApplicationInfo> affects,
            ApplicationEventPublisher applicationEventPublisher);

    private AffectedType getAffectedType(String affectedApplicationName,
            Map<String, Collection<ApplicationInfo>> snapshots) {
        if (latestSnapshots.containsKey(affectedApplicationName)
                && !snapshots.containsKey(affectedApplicationName)) {
            return AffectedType.OFFLINE;
        } else if (!latestSnapshots.containsKey(affectedApplicationName)
                && snapshots.containsKey(affectedApplicationName)) {
            return AffectedType.ONLINE;
        }
        return AffectedType.NONE;
    }

    private AffectedType getAffectedType(ApplicationInfo affectedApplicationInfo,
            Collection<ApplicationInfo> lastApplicationInfos,
            Collection<ApplicationInfo> applicationInfos) {
        if (lastApplicationInfos.contains(affectedApplicationInfo)
                && !applicationInfos.contains(affectedApplicationInfo)) {
            return AffectedType.OFFLINE;
        } else if (!lastApplicationInfos.contains(affectedApplicationInfo)
                && applicationInfos.contains(affectedApplicationInfo)) {
            return AffectedType.ONLINE;
        }
        return AffectedType.NONE;
    }

    private List<AffectedApplicationInfo> hasChanged(
            Map<String, Collection<ApplicationInfo>> snapshots) {
        List<AffectedApplicationInfo> affected = new ArrayList<>();
        Set<String> applicationNames = snapshots.keySet();
        Set<String> lastApplicationNames =
                MapUtils.isNotEmpty(latestSnapshots) ? latestSnapshots.keySet()
                        : Collections.emptySet();
        Collection<String> affectedApplicationNames =
                CollectionUtils.disjunction(applicationNames, lastApplicationNames);
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
            lastApplicationInfos = latestSnapshots.containsKey(applicationName)
                    ? latestSnapshots.get(applicationName)
                    : Collections.emptySet();
            Collection<ApplicationInfo> affectedApplicationInfos =
                    CollectionUtils.disjunction(applicationInfos, lastApplicationInfos);
            if (CollectionUtils.isNotEmpty(affectedApplicationInfos)) {
                for (ApplicationInfo applicationInfo : affectedApplicationInfos) {
                    AffectedApplicationInfo affectedInfo = new AffectedApplicationInfo();
                    affectedInfo.setAffectedType(getAffectedType(applicationInfo,
                            lastApplicationInfos, applicationInfos));
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
