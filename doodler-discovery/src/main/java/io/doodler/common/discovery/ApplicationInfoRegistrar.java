package io.doodler.common.discovery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @Description: ApplicationInfoRegistrar
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ApplicationInfoRegistrar implements ApplicationEventPublisherAware {

	private final ApplicationInfoManager applicationInfoManager;

	private final Heartbeater heartbeater;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${discovery.client.dontRegisterMe:false}")
	private boolean dontRegisterMe;
	
	@Setter
	private ApplicationEventPublisher applicationEventPublisher; 

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady(ApplicationReadyEvent event) {
		if (!dontRegisterMe) {
			ApplicationInfo applicationInfo = applicationInfoManager.registerSelf();
			applicationInfoManager.cleanExpiredSiblings(heartbeater);
			
			applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this, applicationInfo));
		}
	}

	@EventListener(ApplicationInfoRefreshEvent.class)
	public void onApplicationInfoRefreshed(ApplicationInfoRefreshEvent event) {
		if (!dontRegisterMe) {
			applicationInfoManager.registerSelf();
			applicationInfoManager.cleanExpiredSiblings(heartbeater);
		}
	}
}