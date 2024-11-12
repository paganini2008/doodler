package com.github.doodler.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Logs
 * @Author: Fred Feng
 * @Date: 21/03/2023
 * @Version 1.0.0
 */
public abstract class Logs {

	public static final String LOGGER_NAME_TASK_SCHEDULER = "logger-task-scheduler";
	public static final String LOGGER_NAME_ASYNC_OPERATIONS = "logger-async-operations";

	public static final Logger tashScheduler = LoggerFactory.getLogger(LOGGER_NAME_TASK_SCHEDULER);
	public static final Logger asyncOperations = LoggerFactory.getLogger(LOGGER_NAME_ASYNC_OPERATIONS);
	
}