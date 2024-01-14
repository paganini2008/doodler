package com.github.doodler.common.discovery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: AffectedApplicationInfo
 * @Author: Fred Feng
 * @Date: 28/01/2020
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