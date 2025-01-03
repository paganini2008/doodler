package com.github.doodler.common.zk.election;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.Markers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: LeaderElectionContext
 * @Author: Fred Feng
 * @Date: 02/08/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LeaderElectionContext extends LeaderSelectorListenerAdapter implements ManagedBeanLifeCycle,
        ApplicationListener<ApplicationReadyEvent> {

    private final InstanceId instanceId;
    private final CuratorFramework zkClient;
    private final LeaderElectionProperties electionProperties;
    private final List<LeaderElectionListener> electionListeners;
    private CountDownLatch locker;
    private LeaderSelector leaderSelector;

    public void abdicate() {
        if (locker != null) {
            locker.countDown();
            locker = null;
        }
    }

    public boolean isLeader() {
        return leaderSelector != null && leaderSelector.hasLeadership();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        leaderSelector = new LeaderSelector(zkClient, electionProperties.getPath(), this);
        leaderSelector.autoRequeue();

        AnnotationAwareOrderComparator.sort(electionListeners);
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        if (log.isInfoEnabled()) {
            log.info(Markers.SYSTEM, "InstanceId '{}' work on leader role.", instanceId.get());
        }
        if (electionListeners != null) {
            for (LeaderElectionListener electionListener : electionListeners) {
                electionListener.whenTakeLeadership();
            }
        }
        locker = new CountDownLatch(1);
        if (electionProperties.getRulePeriod() > 0) {
            locker.await(electionProperties.getRulePeriod(), TimeUnit.SECONDS);
        } else {
            locker.await();
        }
        if (electionListeners != null) {
            for (LeaderElectionListener electionListener : electionListeners) {
                electionListener.whenAbdicateLeadership();
            }
        }
        if (log.isInfoEnabled()) {
            log.info(Markers.SYSTEM, "InstanceId '{}' abdicate leader role.", instanceId.get());
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (leaderSelector != null) {
            leaderSelector.start();
            if (log.isInfoEnabled()) {
                log.info(Markers.SYSTEM, "InstanceId '{}' is choosing new leader.", instanceId.get());
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (leaderSelector != null) {
            leaderSelector.close();
        }
    }

}
