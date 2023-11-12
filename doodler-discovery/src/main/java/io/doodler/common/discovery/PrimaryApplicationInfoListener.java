package io.doodler.common.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;

import io.doodler.common.discovery.AffectedApplicationInfo.AffectedType;
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
public class PrimaryApplicationInfoListener implements ApplicationEventPublisherAware {

    private final ApplicationInfoManager applicationInfoManager;

    private final ApplicationInfoHolder applicationInfoHolder;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener(ApplicationInfoRegisteredEvent.class)
    public void onApplicationInfoRegistered(ApplicationInfoRegisteredEvent event) {
        ApplicationInfo selfApplicationInfo = applicationInfoHolder.get();
        Collection<ApplicationInfo> candidates = applicationInfoManager.getApplicationInfos(
                selfApplicationInfo.getApplicationName());
        if (CollectionUtils.isEmpty(candidates)) {
            if (log.isWarnEnabled()) {
                log.warn("No primary application selected because of no available applications for name: {}",
                        selfApplicationInfo.getApplicationName());
            }
        }else {
        	doSelectLastOne(candidates);
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
        if (CollectionUtils.isEmpty(offlineApplicationInfos)) {
            return;
        }
        ApplicationInfo primary = applicationInfoHolder.getPrimary();
        if (offlineApplicationInfos.contains(primary)) {
            Collection<ApplicationInfo> candidates = applicationInfoManager.getApplicationInfos(
            		primary.getApplicationName());
            candidates = new ArrayList<>(candidates);
            candidates.removeAll(offlineApplicationInfos);
            if (CollectionUtils.isEmpty(candidates)) {
                if (log.isWarnEnabled()) {
                    log.warn("No primary application selected because of no available applications for name: {}",
                    		primary.getApplicationName());
                }
            }else {
            	doSelectLastOne(candidates);
            }
        }
    }

    private void doSelectLastOne(Collection<ApplicationInfo> candidates) {
        ApplicationInfo primaryApplicationInfo = null;
        for (Iterator<ApplicationInfo> it = candidates.iterator(); it.hasNext(); ) {
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
}