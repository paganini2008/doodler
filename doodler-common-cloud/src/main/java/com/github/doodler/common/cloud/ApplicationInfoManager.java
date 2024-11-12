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

    default void saveMetadata(Map<String, String> data) {
        throw new UnsupportedOperationException("saveMetadata");
    }

    Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf);

    default Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
        Map<String, Collection<ApplicationInfo>> map = getApplicationInfos(true);
        return map.getOrDefault(applicationName, Collections.emptyList());
    }

    Collection<ApplicationInfo> getSiblingApplicationInfos();
}