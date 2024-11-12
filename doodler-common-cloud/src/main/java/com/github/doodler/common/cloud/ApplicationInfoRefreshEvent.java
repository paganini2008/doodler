package com.github.doodler.common.cloud;

/**
 * @Description: ApplicationInfoRefreshEvent
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
public class ApplicationInfoRefreshEvent extends ApplicationInfoEvent {

    private static final long serialVersionUID = 998914075734572211L;

    public ApplicationInfoRefreshEvent(Object source) {
        super(source);
    }
}