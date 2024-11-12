package com.github.doodler.common.cloud.zookeeper;

import static com.github.doodler.common.cloud.redis.CloudConstants.METADATA_APPLICATION_INFO;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientRegistrar;
import com.github.doodler.common.context.ENC;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: ZookeeperDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 09/09/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ZookeeperDiscoveryClientRegistrar implements DiscoveryClientRegistrar {

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        ServiceInstanceRegistration registration =
                event.getApplicationContext().getBean(ServiceInstanceRegistration.class);
        Map<String, String> metadata = registration.getMetadata();
        String appInfoString = metadata.get(METADATA_APPLICATION_INFO);
        if (StringUtils.isNotBlank(appInfoString)) {
            appInfoString = ENC.decrypt(appInfoString);
            applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this,
                    JacksonUtils.parseJson(appInfoString, ApplicationInfo.class)));
        }
    }

}
