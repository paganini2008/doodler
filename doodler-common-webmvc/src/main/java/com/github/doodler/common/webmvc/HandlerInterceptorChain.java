package com.github.doodler.common.webmvc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.github.doodler.common.context.WebMvcInterceptor;
import com.github.doodler.common.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HandlerInterceptorChain
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class HandlerInterceptorChain implements HandlerInterceptor, ApplicationContextAware, SmartInitializingSingleton {

	private final List<WebMvcInterceptor> interceptors = new CopyOnWriteArrayList<>();

	private ApplicationContext applicationContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return getInterceptors().stream().allMatch(i -> {
			try {
				return i.preHandle(request, response, handler);
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
				return false;
			}
		});
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		getInterceptors().forEach(i -> {
			try {
				i.postHandle(request, response, handler);
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
		getInterceptors().forEach(i -> {
			try {
				i.afterCompletion(request, response, handler, e);
			} catch (Exception e1) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}
	
	protected List<WebMvcInterceptor> getInterceptors() {
		return interceptors;
	}

	@Override
	public void afterSingletonsInstantiated() {
		Map<String, WebMvcInterceptor> beans = applicationContext.getBeansOfType(WebMvcInterceptor.class);
		if (MapUtils.isNotEmpty(beans)) {
			this.interceptors.addAll(beans.values());
			this.interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}