package com.github.doodler.common.ws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.github.doodler.common.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WsStateChangeListenerContainer
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class WsStateChangeListenerContainer implements SmartInitializingSingleton, ApplicationContextAware, ApplicationEventPublisherAware {

    private final List<WsStateChangeListener> listeners = new CopyOnWriteArrayList<>();

    private ApplicationContext applicationContext;
    
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

    public void triggerIfOpen(WsSession session) {
        for (WsStateChangeListener listener : listeners) {
            try {
                listener.onOpen(session);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        applicationEventPublisher.publishEvent(new WsStateChangEvent(session, WsStateType.OPENED));
    }

    public void triggerIfClose(WsSession session, int code, String reason) {
        for (WsStateChangeListener listener : listeners) {
            try {
                listener.onClose(session, code, reason);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        applicationEventPublisher.publishEvent(new WsStateChangEvent(session, WsStateType.CLOSED));
    }

    public void triggerIfReceiveMessage(WsUser from, Object data, long timestamp, WsSession session) {
        for (WsStateChangeListener listener : listeners) {
            try {
                if (listener.supportsMessageType(data.getClass())) {
                    listener.onMessage(from, data, timestamp, session);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, WsStateChangeListener> beans = applicationContext.getBeansOfType(WsStateChangeListener.class);
        if (MapUtils.isNotEmpty(beans)) {
            listeners.addAll(beans.values());
            listeners.sort(AnnotationAwareOrderComparator.INSTANCE);
        }
    }
}