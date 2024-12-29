package com.github.doodler.common.cloud;

import org.springframework.context.event.EventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: SiblingApplicationInfoListener
 * @Author: Fred Feng
 * @Date: 05/09/2023
 * @Version 1.0.0
 */
@Slf4j
public class SiblingApplicationInfoListener {

    @EventListener({SiblingApplicationInfoChangeEvent.class})
    public void handleSiblingApplicationInfoChangeEvent(SiblingApplicationInfoChangeEvent event) {
        if (log.isTraceEnabled()) {
            event.getAffectedApplications().forEach(app -> {
                log.trace(app.toString());
            });
        }
    }
}
