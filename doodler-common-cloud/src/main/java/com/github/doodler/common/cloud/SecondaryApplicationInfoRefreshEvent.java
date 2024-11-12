package com.github.doodler.common.cloud;

/**
 * @Description: SecondaryApplicationInfoRefreshEvent
 * @Author: Fred Feng
 * @Date: 20/09/2023
 * @Version 1.0.0
 */
public class SecondaryApplicationInfoRefreshEvent extends ApplicationInfoEvent {

	private static final long serialVersionUID = -358512162264093271L;

	public SecondaryApplicationInfoRefreshEvent(Object source, ApplicationInfo primary) {
		super(source);
		this.primary = primary;
	}

	private final ApplicationInfo primary;

	public ApplicationInfo getPrimary() {
		return primary;
	}
}