package com.github.doodler.common.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.AffectedApplicationInfo.AffectedType;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.SimpleTimer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: PrimaryApplicationInfoListener
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class PrimaryApplicationInfoListener implements ApplicationEventPublisherAware, ManagedBeanLifeCycle {

    private final ApplicationInfoManager applicationInfoManager;

    private final ApplicationInfoHolder applicationInfoHolder;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    private PrimaryApplicationInfoChecker primaryApplicationInfoChecker;

    @EventListener(ApplicationInfoRegisteredEvent.class)
    public void onApplicationInfoRegistered(ApplicationInfoRegisteredEvent event) {
        selectPrimary();
        primaryApplicationInfoChecker.start();
    }

    private void selectPrimary() {
        final String applicationName = applicationInfoHolder.get().getServiceId();
        Collection<ApplicationInfo> candidates = applicationInfoManager.getApplicationInfos(applicationName);
        if (CollectionUtils.isEmpty(candidates)) {
            if (log.isWarnEnabled()) {
                log.warn("No primary application selected because of no available applications for name: {}",
                        applicationName);
            }
        } else {
            doSelectPrimary(candidates);
        }
    }

    @EventListener(SiblingApplicationInfoChangeEvent.class)
    public void onSiblingApplicationInfoChange(SiblingApplicationInfoChangeEvent event) {
        if (CollectionUtils.isEmpty(event.getAffects())) {
            return;
        }
        List<ApplicationInfo> offlineApplicationInfos = event.getAffects().stream().filter(
                a -> a.getAffectedType() == AffectedType.OFFLINE).map(a -> a.getApplicationInfo()).collect(
                        Collectors.toList());
        List<ApplicationInfo> noneApplicationInfos = event.getAffects().stream().filter(
                a -> a.getAffectedType() == AffectedType.NONE).map(a -> a.getApplicationInfo()).collect(
                        Collectors.toList());
        if (CollectionUtils.isNotEmpty(offlineApplicationInfos)) {
            ApplicationInfo primary = applicationInfoHolder.getPrimary();
            if (offlineApplicationInfos.contains(primary)) {
                Collection<ApplicationInfo> candidates = applicationInfoManager.getApplicationInfos(
                        primary.getServiceId());
                candidates = new ArrayList<>(candidates);
                candidates.removeAll(offlineApplicationInfos);
                if (CollectionUtils.isEmpty(candidates)) {
                    if (log.isWarnEnabled()) {
                        log.warn("No primary application selected because of no available applications for name: {}",
                                primary.getServiceId());
                    }
                } else {
                    doSelectPrimary(candidates);
                }
            }
        } else if (CollectionUtils.isNotEmpty(noneApplicationInfos)) {
            ApplicationInfo primary = applicationInfoHolder.getPrimary();
            if (noneApplicationInfos.contains(primary)) {
                applicationEventPublisher.publishEvent(
                        new SecondaryApplicationInfoRefreshEvent(this, primary));
            }
        }

    }

    private synchronized void doSelectPrimary(Collection<ApplicationInfo> candidates) {
        ApplicationInfo primaryApplicationInfo = null;
        for (Iterator<ApplicationInfo> it = candidates.iterator(); it.hasNext();) {
            primaryApplicationInfo = it.next();
        }
        applicationInfoHolder.setPrimary(primaryApplicationInfo);
        if (applicationInfoHolder.isPrimary()) {
            if (log.isInfoEnabled()) {
                log.info("{} is primary.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(new PrimaryApplicationInfoReadyEvent(this));
        } else {
            if (log.isInfoEnabled()) {
                log.info("{} is secondary.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(
                    new SecondaryApplicationInfoRefreshEvent(this, applicationInfoHolder.getPrimary()));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        primaryApplicationInfoChecker = new PrimaryApplicationInfoChecker(15, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (primaryApplicationInfoChecker != null) {
            primaryApplicationInfoChecker.stop();
        }
    }

    /**
     * 
     * @Description: PrimaryApplicationInfoChecker
     * @Author: Fred Feng
     * @Date: 03/05/2024
     * @Version 1.0.0
     */
    private class PrimaryApplicationInfoChecker extends SimpleTimer {

        PrimaryApplicationInfoChecker(long period, TimeUnit timeUnit) {
            super(period, timeUnit);
        }

        @Override
        public boolean change() throws Exception {
            if (applicationInfoHolder.getPrimary() == null) {
                selectPrimary();
            }
            return true;
        }

    }
}