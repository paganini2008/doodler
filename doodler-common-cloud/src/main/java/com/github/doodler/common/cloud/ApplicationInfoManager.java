package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @Description: ApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public interface ApplicationInfoManager {

    default void updateMetadata(Map<String, String> data) {
        throw new UnsupportedOperationException("saveMetadata");
    }

    default Map<String, String> getMetadata() {
        throw new UnsupportedOperationException("getMetadata");
    }

    Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includeSelf);

    default Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
        Map<String, Collection<ApplicationInfo>> map = getApplicationInfos(true);
        return map.getOrDefault(applicationName, Collections.emptyList());
    }

    Collection<ApplicationInfo> getSiblingApplicationInfos();
}
