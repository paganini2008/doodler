package io.doodler.common.discovery;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: ApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
public interface ApplicationInfoManager {

    default ApplicationInfo registerSelf() {
    	return null;
    }

    default List<ApplicationInfo> cleanExpiredSiblings(Heartbeater heartbeater) {
    	return null;
    }

    Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf);
    
	default Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
		Map<String, Collection<ApplicationInfo>> map = getApplicationInfos(true);
		return map.getOrDefault(applicationName, Collections.emptyList());
	}

    Collection<ApplicationInfo> getSiblingApplicationInfos();
}