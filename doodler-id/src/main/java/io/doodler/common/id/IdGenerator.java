package io.doodler.common.id;

/**
 * 
 * @Description: IdGenerator
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public interface IdGenerator {

    long currentId();

    long generateId();
}