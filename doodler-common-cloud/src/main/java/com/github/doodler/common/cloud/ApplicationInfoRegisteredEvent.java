package com.github.doodler.common.cloud;

/**
 * @Description: ApplicationInfoRegisteredEvent
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public class ApplicationInfoRegisteredEvent extends ApplicationInfoEvent {

    private static final long serialVersionUID = -461378654923909596L;

    public ApplicationInfoRegisteredEvent(Object source, ApplicationInfo applicationInfo) {
        super(source);
        this.applicationInfo = applicationInfo;
    }

    private final ApplicationInfo applicationInfo;

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}