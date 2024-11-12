package com.github.doodler.common.cloud;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: AffectedApplicationInfo
 * @Author: Fred Feng
 * @Date: 28/03/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class AffectedApplicationInfo {

    private AffectedType affectedType;
    
    private ApplicationInfo applicationInfo;

    public static enum AffectedType {

        ONLINE, OFFLINE, NONE;
    }
}