package com.github.doodler.common.amqp.eventbus;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: EventObject
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public abstract class EventObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    public EventObject(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}