package com.github.doodler.common.quartz.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobGroupStatusVo
 * @Author: Fred Feng
 * @Date: 29/10/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class JobGroupStatusVo {

	private String jobGroup;
	private long jobCount;
	private long completedJobCount;
	private long runningJobCount;
	private long errorCount;
}