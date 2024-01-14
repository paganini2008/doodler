package com.github.doodler.common.ws;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

/**
 * @Description: LifeCycleCallback
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Order(0)
public class LifeCycleCallback implements WsStateChangeListener {

    @Override
    public void onOpen(WsSession session) {
        session.initialize();
    }

    @Override
    public void onClose(@Nullable WsSession session, int code, String reason) throws IOException {
        if (session != null) {
            session.destroy(reason);
        }
    }

}