package com.github.doodler.common.quartz.scheduler;

import org.springframework.scheduling.SchedulingException;

/**
 * @Description: JobSchedulingException
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
public class JobSchedulingException extends SchedulingException {

	private static final long serialVersionUID = -526454691060364679L;

	public JobSchedulingException(String msg) {
		super(msg);
	}

	public JobSchedulingException(String msg, Throwable cause) {
		super(msg, cause);
	}
}