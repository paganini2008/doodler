package com.github.doodler.common.quartz.scheduler;

import cn.hutool.core.net.NetUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: JobSchedulerStatusNotifier
 * @Author: Fred Feng
 * @Date: 28/09/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class JobSchedulerStatusNotifier {

    private final MeterRegistry meterRegistry;

    private final Map<String, Counter> addedCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> deletedCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> scheduledCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${server.port}")
    private int port;

    private final String localHost = NetUtil.getLocalhostStr();

    @EventListener(SchedulerStateChangeEvent.class)
    public void onSchedulerStateChange(SchedulerStateChangeEvent event) {
        Object parameter = event.getParameter();
        Counter counter = null;
        switch (event.getSchedulerStateEventType()) {
            case JOB_ADDED:
                String jobGroup = ((JobDetail) parameter).getKey().getGroup();
                counter = MapUtils.getOrCreate(addedCounters, jobGroup,
                        () -> createCounter("quartz.job.added.count", jobGroup));
                break;
            case JOB_DELETED:
                jobGroup = ((JobKey) parameter).getGroup();
                counter = MapUtils.getOrCreate(deletedCounters, jobGroup,
                        () -> createCounter("quartz.job.deleted.count", jobGroup));
                break;
            case JOB_SCHEDULED:
                jobGroup = ((Trigger) parameter).getJobKey().getGroup();
                counter = MapUtils.getOrCreate(scheduledCounters, jobGroup,
                        () -> createCounter("quartz.job.scheduled.count", jobGroup));
                break;
            case SCHEDULER_ERROR:
                if (parameter instanceof SpecificJobExecutionException) {
                    jobGroup = ((SpecificJobExecutionException) parameter).getJobKey().getGroup();
                    counter = MapUtils.getOrCreate(errorCounters, jobGroup,
                            () -> createCounter("quartz.scheduler.error.count", jobGroup));
                }
                break;
            default:
                break;
        }
        if (counter != null) {
            counter.increment();
        }
    }

    private Counter createCounter(String name, String jobGroup) {
        return meterRegistry.counter(name,
                Arrays.asList(Tag.of("job_group", jobGroup),
                        Tag.of("env", env),
                        Tag.of("instance", localHost + ":" + port)));
    }
}