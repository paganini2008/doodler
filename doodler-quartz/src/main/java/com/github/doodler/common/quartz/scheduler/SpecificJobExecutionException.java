package com.github.doodler.common.quartz.scheduler;

import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @Description: SpecificJobExecutionException
 * @Author: Fred Feng
 * @Date: 29/09/2023
 * @Version 1.0.0
 */
public class SpecificJobExecutionException extends JobExecutionException {

    private static final long serialVersionUID = 4701383429988684534L;

    private final JobKey jobKey;

    public SpecificJobExecutionException(String msg, Throwable e, boolean refireImmediately, JobKey jobKey) {
        super(msg, e, refireImmediately);
        this.jobKey = jobKey;
    }

    public SpecificJobExecutionException(Throwable e, boolean refireImmediately, JobKey jobKey) {
        super(e, refireImmediately);
        this.jobKey = jobKey;
    }

    public JobKey getJobKey() {
        return jobKey;
    }
}