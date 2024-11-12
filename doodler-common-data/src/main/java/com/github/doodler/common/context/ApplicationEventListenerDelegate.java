package com.github.doodler.common.context;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SmartApplicationListener;
import com.github.doodler.common.utils.LangUtils;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: ApplicationEventListenerDelegate
 * @Author: Fred Feng
 * @Date: 29/03/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class ApplicationEventListenerDelegate implements SmartApplicationListener {

	private final Class<?>[] eventTypes;

	private final Map<Type, List<ApplicationListener>> applicationListeners = new ConcurrentHashMap<>();

	public ApplicationEventListenerDelegate(Class<?>[] eventTypes) {
		this.eventTypes = eventTypes;
	}

	public void addListener(ApplicationListener<? extends ApplicationEvent> applicationListener) {
		Class<?> type = LangUtils.findParameterizedType(applicationListener.getClass(), ApplicationListener.class);
		MapUtils.getOrCreate(applicationListeners, type, CopyOnWriteArrayList::new).add(applicationListener);
	}

	public void removeListener(ApplicationListener<? extends ApplicationEvent> applicationListener) {
		Class<?> type = LangUtils.findParameterizedType(applicationListener.getClass(), ApplicationListener.class);
		if (applicationListeners.containsKey(type)) {
			applicationListeners.get(type).remove(applicationListener);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (applicationListeners.containsKey(event.getClass())) {
			applicationListeners.get(event.getClass()).forEach(l -> {
				l.onApplicationEvent(event);
			});
		}
	}

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return ArrayUtils.contains(eventTypes, eventType);
	}
}