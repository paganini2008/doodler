package com.github.doodler.common.discovery;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: DiscoveryClientService
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
public interface DiscoveryClientService {
	
	default Collection<String> getApplicationNames(){
		return Collections.unmodifiableCollection(getApplicationInfos().keySet());
	}

    default Optional<ApplicationInfo> getApplicationInfo(String applicationName, String host, int port) {
        return getApplicationInfos(applicationName).stream().filter(
                info -> info.getHost().equals(host) && info.getPort() == port).findFirst();
    }

    Collection<ApplicationInfo> getApplicationInfos(String applicationName);

    Map<String, Collection<ApplicationInfo>> getApplicationInfos();
}