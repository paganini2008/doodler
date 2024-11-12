package com.github.doodler.common.quartz.scheduler;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: TriggerStatusVo
 * @Author: Fred Feng
 * @Date: 20/09/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class TriggerStatusVo {

    private String jobName;
    private String jobGroup;
    private String triggerName;
    private String triggerGroup;
    private String description;
    private Date nextFireTime;
    private Date prevFireTime;
    private Date finalFireTime;
    private int priority;
    private String state;
    private String type;
    private Date startTime;
    private Date endTime;
    private String calendarName;
    private int misfireInstr;
}