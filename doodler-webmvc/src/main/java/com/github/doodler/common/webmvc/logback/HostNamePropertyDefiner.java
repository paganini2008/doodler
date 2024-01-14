package com.github.doodler.common.webmvc.logback;

import ch.qos.logback.core.PropertyDefinerBase;
import cn.hutool.core.net.NetUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: HostNamePropertyDefiner
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
public class HostNamePropertyDefiner extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        String hostName = NetUtil.getLocalhostStr();
        if (StringUtils.isBlank(hostName)) {
            hostName = "localhost";
        }
        return hostName.concat(":");
    }
}