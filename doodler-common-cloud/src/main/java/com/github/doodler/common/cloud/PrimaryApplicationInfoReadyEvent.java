package com.github.doodler.common.cloud;

/**
 * @Description: PrimaryApplicationInfoReadyEvent
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public class PrimaryApplicationInfoReadyEvent extends ApplicationInfoEvent {

	private static final long serialVersionUID = -7997266136652774244L;

	public PrimaryApplicationInfoReadyEvent(Object source) {
		super(source);
	}
	
}