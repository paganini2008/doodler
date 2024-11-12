package com.github.doodler.common.cloud;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: ApplicationInfoEvent
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public abstract class ApplicationInfoEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ApplicationInfoEvent(Object source) {
		super(source);
	}
}