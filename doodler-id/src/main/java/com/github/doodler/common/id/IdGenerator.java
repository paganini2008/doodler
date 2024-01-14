package com.github.doodler.common.id;

/**
 * 
 * @Description: IdGenerator
 * @Author: Fred Feng
 * @Date: 16/07/2020
 * @Version 1.0.0
 */
public interface IdGenerator {

    long currentId();

    long generateId();
}