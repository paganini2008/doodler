package com.github.doodler.common.id;

/**
 * 
 * @Description: AbstractIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public abstract class AbstractIdGenerator implements IdGenerator {

    protected Long lastId;

    @Override
    public Long getLastId() {
        return lastId;
    }

}
