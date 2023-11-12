package io.doodler.common.discovery;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @Description: DefaultDiscoveryClientService
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public class DefaultDiscoveryClientService implements DiscoveryClientService {

    private final HeartbeatChecker heartbeatChecker;

    public DefaultDiscoveryClientService(HeartbeatChecker heartbeatChecker) {
        this.heartbeatChecker = heartbeatChecker;
    }

    @Override
    public Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
        Map<String, Collection<ApplicationInfo>> all = getApplicationInfos();
        if (!all.containsKey(applicationName)) {
            return Collections.emptySet();
        }
        return all.get(applicationName);
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos() {
        return heartbeatChecker.getLatestSnapshots();
    }
}