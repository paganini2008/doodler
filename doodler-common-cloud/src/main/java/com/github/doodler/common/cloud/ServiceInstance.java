package com.github.doodler.common.cloud;

import org.springframework.lang.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: ServiceInstance
 * @Author: Fred Feng
 * @Date: 13/08/2024
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString(exclude = {"serviceDetail"})
public class ServiceInstance {

    private String serviceId;
    private String url;
    private String contextPath;
    private boolean online;

    private @Nullable Object serviceDetail;
}
