package com.github.doodler.common.transmitter;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.AffectedApplicationInfo;
import com.github.doodler.common.cloud.AffectedApplicationInfo.AffectedType;
import com.github.doodler.common.cloud.SiblingApplicationInfoChangeEvent;
import com.github.doodler.common.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: NioClientStarter
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class NioClientBootstrap {

    private final NioClient nioClient;

    @EventListener(SiblingApplicationInfoChangeEvent.class)
    public void onSiblingApplicationInfoChangeEvent(SiblingApplicationInfoChangeEvent event) {
        Collection<AffectedApplicationInfo> affectedApplications = event.getAffects();
        if (CollectionUtils.isNotEmpty(affectedApplications)) {
            affectedApplications.stream()
                    .filter(app -> app.getAffectedType().equals(AffectedType.ONLINE))
                    .forEach(app -> {
                        Map<String, String> metadata = app.getApplicationInfo().getMetadata();
                        if (MapUtils.isEmpty(metadata)) {
                            log.info("No metadata in app: {}", app);
                            return;
                        }
                        String serverLocation = (String) metadata
                                .get(TransmitterConstants.TRANSMITTER_SERVER_LOCATION);
                        if (StringUtils.isBlank(serverLocation)) {
                            log.info("No 'TRANSMITTER_SERVER_LOCATION' in metadata: {}", metadata);
                            return;
                        }
                        int index = serverLocation.indexOf(":");
                        String hostName = serverLocation.substring(0, index);
                        int port = Integer.parseInt(serverLocation.substring(index + 1));
                        nioClient.connect(InetSocketAddress.createUnresolved(hostName, port),
                                addr -> {
                                    log.info("Successfully connected to address: {}",
                                            addr.toString());
                                });
                    });
        }
    }



}
