package com.github.doodler.common.quartz.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobLogQuery
 * @Author: Fred Feng
 * @Date: 20/10/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class JobLogQuery {

    private int pageNumber = 1;
    private int pageSize = 20;
    private int status = -1;

    private String jobGroup;
    private String jobName;
    private String triggerName;
    private String triggerGroup;
}