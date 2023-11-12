package io.doodler.common.quartz.scheduler;

import cn.hutool.core.net.NetUtil;
import io.doodler.common.enums.AppName;
import io.doodler.common.utils.MultiMapMap;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

/**
 * @Description: JobSchedulerMeterCounter
 * @Author: Fred Feng
 * @Date: 28/09/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class JobSchedulerMeterCounter implements InitializingBean {

    private static final String[] DEFAULT_METRIC_NAMES = {
            "quartz.job.added.count",
            "quartz.job.deleted.count",
            "quartz.job.scheduled.count",
            "quartz.scheduler.error.count"
    };

    private final MeterRegistry meterRegistry;

    private final MultiMapMap<String, String, Counter> mmap = new MultiMapMap<>();

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${server.port}")
    private int port;

    private final String localHost = NetUtil.getLocalhostStr();

    @Override
    public void afterPropertiesSet() throws Exception {
        for (AppName appName : AppName.values()) {
            for (String metricName : DEFAULT_METRIC_NAMES.clone()) {
                Counter counter = meterRegistry.counter(metricName,
                        Arrays.asList(Tag.of("job_group", appName.getFullName().toUpperCase()),
                                Tag.of("env", env),
                                Tag.of("instance", localHost + ":" + port)));
                mmap.put(metricName, appName.getFullName().toUpperCase(), counter);
            }
        }
    }

    @EventListener(SchedulerStateChangeEvent.class)
    public void onSchedulerStateChange(SchedulerStateChangeEvent event) {
        Object parameter = event.getParameter();
        String jobGroup;
        Counter counter = null;
        switch (event.getSchedulerStateEventType()) {
            case JOB_ADDED:
                jobGroup = ((JobDetail) parameter).getKey().getGroup();
                counter = mmap.get("quartz.job.added.count", jobGroup);
                break;
            case JOB_DELETED:
                jobGroup = ((JobKey) parameter).getGroup();
                counter = mmap.get("quartz.job.deleted.count", jobGroup);
                break;
            case JOB_SCHEDULED:
                jobGroup = ((Trigger) parameter).getJobKey().getGroup();
                counter = mmap.get("quartz.job.scheduled.count", jobGroup);
                break;
            case SCHEDULER_ERROR:
                if (parameter instanceof SpecificJobExecutionException) {
                    jobGroup = ((SpecificJobExecutionException) parameter).getJobKey().getGroup();
                    counter = mmap.get("quartz.scheduler.error.count", jobGroup);
                }
                break;
            default:
                break;
        }
        if (counter != null) {
            counter.increment();
        }
    }
}