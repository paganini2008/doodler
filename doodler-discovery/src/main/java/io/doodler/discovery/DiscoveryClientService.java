package io.doodler.discovery;

import java.util.Map;
import java.util.Set;

/**
 * @Description: DiscoveryClientService
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public interface DiscoveryClientService {

    Set<ApplicationInfo> getApplicationInfos(String applicationName);
    
    Map<String, Set<ApplicationInfo>> getApplicationInfos();
}