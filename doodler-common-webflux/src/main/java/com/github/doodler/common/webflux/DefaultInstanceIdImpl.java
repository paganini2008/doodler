package com.github.doodler.common.webflux;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.utils.IdUtils;
import com.github.doodler.common.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: InstanceId
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public final class DefaultInstanceIdImpl implements InstanceId, InitializingBean {

    private static final String DEFAULT_ID_PATTERN = "INS-%s@%s:%s@%s";

    private final AtomicBoolean standby = new AtomicBoolean();

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private int port;

    private String id;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.id = String.format(DEFAULT_ID_PATTERN, IdUtils.getShortUuid(),
                NetUtils.getLocalHostAddress(), port, applicationName);
    }

    public String get() {
        return id;
    }

    public boolean isStandby() {
        return standby.get();
    }

    public void setStandBy(boolean value) {
        standby.set(value);
    }

    @EventListener({ApplicationReadyEvent.class})
    public void onApplicationReadyEvent() {
        standby.set(true);
        if (log.isInfoEnabled()) {
            log.info("[{}] Web application is ready to accept connections ...", id);
        }
    }
}
