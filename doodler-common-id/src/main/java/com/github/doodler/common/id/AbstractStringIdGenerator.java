package com.github.doodler.common.id;

/**
 * 
 * @Description: AbstractStringIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public abstract class AbstractStringIdGenerator implements StringIdGenerator {

    protected String lastId;

    @Override
    public String getLastId() {
        return lastId;
    }
}
