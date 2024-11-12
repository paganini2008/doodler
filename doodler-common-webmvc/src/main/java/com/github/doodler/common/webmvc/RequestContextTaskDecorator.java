package com.github.doodler.common.webmvc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskDecorator;
import com.github.doodler.common.context.RequestContextExchanger;
import com.github.doodler.common.utils.MapUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RequestContextTaskDecorator
 * @Author: Fred Feng
 * @Date: 21/09/2023
 * @Version 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public class RequestContextTaskDecorator implements TaskDecorator, SmartInitializingSingleton, ApplicationContextAware {

	private final List<RequestContextExchanger> exchangers = new CopyOnWriteArrayList<>();

	@Setter
	private ApplicationContext applicationContext;

	@Override
	public Runnable decorate(Runnable runnable) {
		try {
			final Object[] context = exchangers.stream().map(e -> e.get()).toArray();
			return () -> {
				try {
					for (int i = 0; i < context.length; i++) {
						exchangers.get(i).set(context[i]);
					}
					runnable.run();
				} finally {
					for (RequestContextExchanger exchanger : exchangers) {
						exchanger.reset();
					}
				}
			};
		} catch (RuntimeException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			return runnable;
		}
	}

	@Override
	public void afterSingletonsInstantiated() {
		Map<String, RequestContextExchanger> map = applicationContext.getBeansOfType(RequestContextExchanger.class);
		if (MapUtils.isNotEmpty(map)) {
			exchangers.addAll(map.values());
		}
	}
}