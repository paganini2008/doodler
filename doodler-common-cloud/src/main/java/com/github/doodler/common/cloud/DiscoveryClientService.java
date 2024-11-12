package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: DiscoveryClientService
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public interface DiscoveryClientService {

    default Collection<String> getExclusiveApplicationNames() {
        return Collections.unmodifiableCollection(getExclusiveApplicationInfos().keySet());
    }

    default Optional<ApplicationInfo> getApplicationInfo(String applicationName, String host, int port) {
        return getApplicationInfos(applicationName).stream().filter(
                info -> info.getHost().equals(host) && info.getPort() == port).findFirst();
    }

    Collection<ApplicationInfo> getApplicationInfos(String applicationName);

    Map<String, Collection<ApplicationInfo>> getExclusiveApplicationInfos();

    Map<String, Collection<ApplicationInfo>> getSiblingApplicationInfos();
}