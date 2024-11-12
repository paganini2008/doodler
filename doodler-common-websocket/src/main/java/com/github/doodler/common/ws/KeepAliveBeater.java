package com.github.doodler.common.ws;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.utils.SimpleTimer;
import com.github.doodler.common.ws.handler.SessionContext;
import com.github.doodler.common.ws.handler.UserSessionContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: KeepAliveBeater
 * @Author: Fred Feng
 * @Date: 23/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class KeepAliveBeater extends SimpleTimer {

    private final SessionContext sessionContext;
    private final UserSessionContext userSessionContext;
    private final WsServerProperties serverConfig;

    public KeepAliveBeater(SessionContext sessionContext, UserSessionContext userSessionContext,
                           WsServerProperties serverConfig) {
        super(serverConfig.getIdleCheckInterval(), TimeUnit.MILLISECONDS);
        this.sessionContext = sessionContext;
        this.userSessionContext = userSessionContext;
        this.serverConfig = serverConfig;
    }

    @Override
    public boolean change() throws Exception {
        doSendPing(sessionContext.getAllWsSessions());
        doSendPing(userSessionContext.getAllWsSessions());
        return true;
    }

    private void doSendPing(List<WsSession> wsSessions) {
    	if(wsSessions.isEmpty()) {
    		return;
    	}
        wsSessions.forEach(session -> {
            long diff;
            if (session.getLastSentTimeMillis() > 0
                    && (diff = System.currentTimeMillis() - session.getLastSentTimeMillis()) > serverConfig.getIdleTimeout()) {
                if (diff > serverConfig.getMaxIdleTimeout()) {
                    try {
                        session.destroy("Too long idle timeout");
                    } catch (IOException e) {
                        if (log.isErrorEnabled()) {
                            log.error(e.getMessage(), e);
                        }
                    }
                } else {
                    try {
                        session.sendPing();
                    } catch (IOException e) {
                        if (log.isErrorEnabled()) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });
    }
}