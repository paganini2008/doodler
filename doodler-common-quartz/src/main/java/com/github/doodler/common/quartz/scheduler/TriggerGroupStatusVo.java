package com.github.doodler.common.quartz.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: TriggerGroupStatusVo
 * @Author: Fred Feng
 * @Date: 29/10/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class TriggerGroupStatusVo {

	private String triggerGroup;
	private long triggerCount;
	private boolean paused;
}