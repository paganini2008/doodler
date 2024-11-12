package com.github.doodler.common.id;

import java.util.UUID;

/**
 * 
 * @Description: SimpleStringIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public class SimpleStringIdGenerator extends AbstractStringIdGenerator {

    @Override
    public String getNextId() {
        return (lastId = UUID.randomUUID().toString());
    }

}
