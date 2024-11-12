package com.github.doodler.common.cloud.eureka;

import static com.github.doodler.common.cloud.redis.CloudConstants.METADATA_APPLICATION_INFO;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientRegistrar;
import com.github.doodler.common.utils.JacksonUtils;
import com.netflix.appinfo.ApplicationInfoManager;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: EurekaDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class EurekaDiscoveryClientRegistrar implements DiscoveryClientRegistrar {

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        ApplicationInfoManager appInfoManager = event.getApplicationContext().getBean(ApplicationInfoManager.class);
        Map<String, String> metadata = appInfoManager.getInfo().getMetadata();
        String json = metadata.get(METADATA_APPLICATION_INFO);
        if (StringUtils.isNotBlank(json)) {
            applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this, JacksonUtils.parseJson(json,
                    ApplicationInfo.class)));
        }
    }

}
