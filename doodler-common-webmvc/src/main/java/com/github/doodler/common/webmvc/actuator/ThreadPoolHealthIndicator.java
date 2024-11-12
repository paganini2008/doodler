package com.github.doodler.common.webmvc.actuator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: ThreadPoolHealthIndicator
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "management.health.threadPool.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class ThreadPoolHealthIndicator extends AbstractHealthIndicator {

    @Lazy
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Lazy
    @Autowired(required = false)
    private ThreadPoolTaskScheduler taskScheduler;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        builder.up()
                .withDetail("taskExecutor", getTaskExecutorInfoMap());
        Map<String, Object> infoMap = getTaskSchedulerInfoMap();
        if (MapUtils.isNotEmpty(infoMap)) {
            builder.withDetail("taskScheduler", infoMap);
        }
        builder.build();
    }

    private Map<String, Object> getTaskExecutorInfoMap() {
        int corePoolSize = taskExecutor.getCorePoolSize();
        int poolSize = taskExecutor.getPoolSize();
        int maxPoolSize = taskExecutor.getMaxPoolSize();
        int activeCount = taskExecutor.getActiveCount();
        long completedTaskCount = taskExecutor.getThreadPoolExecutor().getCompletedTaskCount();
        int largestPoolSize = taskExecutor.getThreadPoolExecutor().getLargestPoolSize();
        long taskCount = taskExecutor.getThreadPoolExecutor().getTaskCount();
        BlockingQueue<Runnable> queue = taskExecutor.getThreadPoolExecutor().getQueue();
        int remainingCapacity = queue.remainingCapacity();
        int queueSize = queue.size();

        Map<String, Object> infoMap = new LinkedHashMap<>();
        infoMap.put("corePoolSize", corePoolSize);
        infoMap.put("poolSize", poolSize);
        infoMap.put("maxPoolSize", maxPoolSize);
        infoMap.put("activeCount", activeCount);
        infoMap.put("completedTaskCount", completedTaskCount);
        infoMap.put("largestPoolSize", largestPoolSize);
        infoMap.put("remainingCapacity", remainingCapacity);
        infoMap.put("queueSize", queueSize);
        infoMap.put("taskCount", taskCount);
        return infoMap;
    }

    private Map<String, Object> getTaskSchedulerInfoMap() {
        if (taskScheduler == null) {
            return Collections.emptyMap();
        }
        int corePoolSize = taskScheduler.getScheduledThreadPoolExecutor().getCorePoolSize();
        int poolSize = taskScheduler.getPoolSize();
        int maxPoolSize = taskScheduler.getScheduledThreadPoolExecutor().getMaximumPoolSize();
        int activeCount = taskScheduler.getActiveCount();
        long completedTaskCount = taskScheduler.getScheduledThreadPoolExecutor().getCompletedTaskCount();
        int largestPoolSize = taskScheduler.getScheduledThreadPoolExecutor().getLargestPoolSize();
        long taskCount = taskScheduler.getScheduledThreadPoolExecutor().getTaskCount();
        BlockingQueue<Runnable> queue = taskScheduler.getScheduledThreadPoolExecutor().getQueue();
        int remainingCapacity = queue.remainingCapacity();
        int queueSize = queue.size();

        Map<String, Object> infoMap = new LinkedHashMap<>();
        infoMap.put("corePoolSize", corePoolSize);
        infoMap.put("poolSize", poolSize);
        infoMap.put("maxPoolSize", maxPoolSize);
        infoMap.put("activeCount", activeCount);
        infoMap.put("completedTaskCount", completedTaskCount);
        infoMap.put("largestPoolSize", largestPoolSize);
        infoMap.put("remainingCapacity", remainingCapacity);
        infoMap.put("queueSize", queueSize);
        infoMap.put("taskCount", taskCount);
        return infoMap;
    }
}