package com.github.doodler.common.id;

/**
 * 
 * @Description: IdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public interface IdGenerator {

    Long getLastId();

    Long getNextId();
}
