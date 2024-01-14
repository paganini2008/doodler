package com.github.doodler.common.quartz.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobStatusVo
 * @Author: Fred Feng
 * @Date: 22/10/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class JobStatusVo {

    private String jobName;
    private String jobGroup;
    private String triggerName;
    private String triggerGroup;
    private String state;
    private String type;
}