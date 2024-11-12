package com.github.doodler.common.cache;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Marker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.github.doodler.common.utils.Markers;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: CacheControl
 * @Author: Fred Feng
 * @Date: 22/09/2023
 * @Version 1.0.0
 */
@Slf4j
public class CacheControl implements InitializingBean {

	private final AtomicBoolean control = new AtomicBoolean();

	@Value("${spring.cache.control:true}")
	private boolean enabled;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private Marker marker;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.control.set(enabled);
		this.marker = Markers.forName(applicationName);
		if(log.isInfoEnabled()) {
			log.info("CacheControl is {}", control.get());
		}
	}

	public void turnOn() {
		control.set(true);
		if (log.isInfoEnabled()) {
			log.info(marker, "Turn on the CacheControl to enable caching tech.");
		}
	}

	public void turnOff() {
		control.set(false);
		if (log.isInfoEnabled()) {
			log.info(marker, "Turn off the CacheControl to disable caching tech.");
		}
	}
	
	public boolean isEnabled() {
		return control.get();
	}
}