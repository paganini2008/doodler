package io.doodler.quartz.scheduler;

/**
 * @Description: JobOperationsException
 * @Author: Fred Feng
 * @Date: 19/06/2023
 * @Version 1.0.0
 */
public class JobOperationsException extends RuntimeException {

	private static final long serialVersionUID = 5409564909469394174L;

	public JobOperationsException(String msg) {
		super(msg);
	}
}