package com.github.doodler.common.quartz;

/**
 * @Description: JobConstants
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public interface JobConstants {

    String DEFAULT_JOB_SERVICE_NAME = "doodler-quartz-service";

    String DEFAULT_JOB_GROUP_NAME = "DEFAULT-JOB-GROUP";

    String DEFAULT_JOB_TRIGGER_GROUP_NAME = "DEFAULT-TRIGGER-GROUP";

    String JOB_EXECUTOR_HTTP_HEADERS = "job-executor-http-headers";
}
