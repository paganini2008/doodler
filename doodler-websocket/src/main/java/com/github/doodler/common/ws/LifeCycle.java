package com.github.doodler.common.ws;

import java.io.IOException;

/**
 * @Description: LifeCycle
 * @Author: Fred Feng
 * @Date: 24/03/2021
 * @Version 1.0.0
 */
public interface LifeCycle {

    default void initialize() {
    }

    void disconnect(String reason) throws IOException;

    default void destroy(String reason) throws IOException {
        disconnect(reason);
    }
}