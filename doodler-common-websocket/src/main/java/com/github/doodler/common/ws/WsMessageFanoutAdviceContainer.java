package com.github.doodler.common.ws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: WsMessageFanoutAdviceContainer
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
public class WsMessageFanoutAdviceContainer implements SmartInitializingSingleton, ApplicationContextAware {

	private final List<WsMessageFanoutAdvice> advices = new CopyOnWriteArrayList<>();
	private ApplicationContext ctx;

	public Object triggerPreFanout(WsUser user, Object payload) {
		Object result = payload;
		for (WsMessageFanoutAdvice advice : advices) {
			if (advice.preSupports(user, result)) {
				result = advice.preFanout(user, result);
			}
		}
		return result;
	}

	public void triggerPostFanout(WsUser from, Object data, long timestamp, WsSession session) {
		advices.forEach(advice -> {
			if (advice.postSupports(from, data, timestamp)) {
				advice.postFanout(from, data, timestamp, session);
			}
		});
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	@Override
	public void afterSingletonsInstantiated() {
		Map<String, WsMessageFanoutAdvice> adviceBeans = ctx.getBeansOfType(WsMessageFanoutAdvice.class);
		if (MapUtils.isNotEmpty(adviceBeans)) {
			advices.addAll(adviceBeans.values());
			advices.sort(AnnotationAwareOrderComparator.INSTANCE);
		}
	}
}