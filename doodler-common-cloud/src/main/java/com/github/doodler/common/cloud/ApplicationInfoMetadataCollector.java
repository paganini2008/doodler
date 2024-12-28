package com.github.doodler.common.cloud;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import com.github.doodler.common.cloud.redis.CloudConstants;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: ApplicationInfoMetadataCollector
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class ApplicationInfoMetadataCollector implements MetadataCollector {

    private final ApplicationInfoHolder applicationInfoHolder;

    @Override
    public Map<String, String> getInitialData() {
        String appInfoStr = JacksonUtils.toJsonString(applicationInfoHolder.get());
        appInfoStr = Base64.encodeBase64String(appInfoStr.getBytes());
        return Collections.singletonMap(CloudConstants.METADATA_APPLICATION_INFO, appInfoStr);
    }



}
